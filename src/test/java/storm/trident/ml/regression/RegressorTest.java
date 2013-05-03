package storm.trident.ml.regression;

import java.util.List;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.DatasetUtils;

public abstract class RegressorTest {

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
	protected double eval(Regressor regressor, List<Instance<Double>> samples) {
		double error = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Instance<Double>> training = DatasetUtils.getTrainingFolds(i, FOLD_NB, samples);
			List<Instance<Double>> eval = DatasetUtils.getEvalFold(i, FOLD_NB, samples);
			error += this.eval(regressor, training, eval);
		}

		return error / FOLD_NB;
	}

	protected double eval(Regressor regressor, List<Instance<Double>> training, List<Instance<Double>> eval) {
		regressor.reset();

		// Train
		for (Instance<Double> sample : training) {
			regressor.update(sample.label, sample.features);
		}

		// Evaluate
		double rmse = 0.0;
		Double actualPrediction;
		for (Instance<Double> sample : eval) {
			actualPrediction = regressor.predict(sample.features);
			rmse += Math.pow(actualPrediction - sample.label, 2);
			System.out.println("Was " + sample.label + ", Found " + actualPrediction);
		}

		return Math.sqrt(rmse / eval.size());
	}

}
