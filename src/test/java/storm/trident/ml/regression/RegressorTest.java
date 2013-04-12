package storm.trident.ml.regression;

import java.util.List;

import storm.trident.ml.testing.data.DatasetUtils;
import storm.trident.ml.testing.data.Sample;

public class RegressorTest {

	private final static Integer FOLD_NB = 10;

	/**
	 * Cross validation with 10 folds
	 * 
	 * @param <L>
	 * @param <F>
	 * @param classifier
	 * @param samples
	 * @return
	 */
	protected double eval(Regressor regressor, List<Sample<Double>> samples) {
		double error = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Sample<Double>> training = DatasetUtils.getTrainingFolds(i, FOLD_NB, samples);
			List<Sample<Double>> eval = DatasetUtils.getEvalFold(i, FOLD_NB, samples);
			error += this.eval(regressor, training, eval);
		}

		return error / FOLD_NB;
	}

	protected double eval(Regressor regressor, List<Sample<Double>> training, List<Sample<Double>> eval) {
		regressor.reset();

		// Train
		for (Sample<Double> sample : training) {
			regressor.update(sample.label, sample.features);
		}

		// Evaluate
		double rmse = 0.0;
		Double actualPrediction;
		for (Sample<Double> sample : eval) {
			actualPrediction = regressor.predict(sample.features);
			rmse += Math.pow(actualPrediction - sample.label, 2);
			System.out.println("Was " + sample.label + ", Found " + actualPrediction);
		}

		return Math.sqrt(rmse / eval.size());
	}

}
