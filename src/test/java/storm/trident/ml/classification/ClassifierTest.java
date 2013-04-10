package storm.trident.ml.classification;

import java.util.List;

import storm.trident.ml.testing.data.DatasetUtils;
import storm.trident.ml.testing.data.Sample;

public class ClassifierTest {

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
	protected <L, F> double eval(Classifier<L, F> classifier, List<Sample<L, F>> samples) {
		double error = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Sample<L, F>> training = DatasetUtils.getTrainingFolds(i, FOLD_NB, samples);
			List<Sample<L, F>> eval = DatasetUtils.getEvalFold(i, FOLD_NB, samples);
			error += this.eval(classifier, training, eval);
		}

		return error / FOLD_NB;
	}

	protected <L, F> double eval(Classifier<L, F> classifier, List<Sample<L, F>> training, List<Sample<L, F>> eval) {
		classifier.reset();

		// Train
		for (Sample<L, F> sample : training) {
			classifier.update(sample.label, sample.features);
		}

		// Evaluate
		double errorCount = 0.0;
		L actualLabel;
		for (Sample<L, F> sample : eval) {
			actualLabel = classifier.classify(sample.features);
			if (!sample.label.equals(actualLabel)) {
				errorCount++;
			}
		}

		return errorCount / eval.size();
	}

}
