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
	protected <L> double eval(Classifier<L> classifier, List<Sample<L>> samples) {
		double error = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Sample<L>> training = DatasetUtils.getTrainingFolds(i, FOLD_NB, samples);
			List<Sample<L>> eval = DatasetUtils.getEvalFold(i, FOLD_NB, samples);
			error += this.eval(classifier, training, eval);
		}

		return error / FOLD_NB;
	}

	protected <L> double eval(Classifier<L> classifier, List<Sample<L>> training, List<Sample<L>> eval) {
		classifier.reset();

		// Train
		for (Sample<L> sample : training) {
			classifier.update(sample.label, sample.features);
		}

		// Evaluate
		double errorCount = 0.0;
		L actualLabel;
		for (Sample<L> sample : eval) {
			actualLabel = classifier.classify(sample.features);
			if (!sample.label.equals(actualLabel)) {
				errorCount++;
			}
		}

		return errorCount / eval.size();
	}

}
