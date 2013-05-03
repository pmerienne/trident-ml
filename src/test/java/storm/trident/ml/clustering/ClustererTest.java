package storm.trident.ml.clustering;

import java.util.List;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.DatasetUtils;

public abstract class ClustererTest {

	private final static Integer FOLD_NB = 10;

	/**
	 * Cross validation with 10 folds
	 * 
	 * @param
	 * @param <F>
	 * @param clusterer
	 * @param samples
	 * @return
	 */
	protected double eval(Clusterer clusterer, List<Instance<Integer>> samples) {
		double randIndex = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Instance<Integer>> training = DatasetUtils.getTrainingFolds(i, FOLD_NB, samples);
			List<Instance<Integer>> eval = DatasetUtils.getEvalFold(i, FOLD_NB, samples);
			randIndex += this.eval(clusterer, training, eval);
		}

		return randIndex / FOLD_NB;
	}

	protected double eval(Clusterer clusterer, List<Instance<Integer>> training, List<Instance<Integer>> eval) {
		clusterer.reset();

		// Train
		for (Instance<Integer> sample : training) {
			clusterer.update(sample.features);
		}

		RandEvaluator randEvaluator = new RandEvaluator();
		double randIndex = randEvaluator.evaluate(clusterer, eval);
		return randIndex;
	}

}
