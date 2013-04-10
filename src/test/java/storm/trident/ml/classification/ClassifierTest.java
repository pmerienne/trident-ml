package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.testing.data.Sample;

public class ClassifierTest {

	private final static Integer FOLD_NB = 10;

	/**
	 * 10 folds validation
	 * @param <L>
	 * @param <F>
	 * @param classifier
	 * @param samples
	 * @return
	 */
	protected <L, F> double eval(Classifier<L, F> classifier, List<Sample<L, F>> samples) {
		double error = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Sample<L, F>> training = this.getTrainingFolds(i, FOLD_NB, samples);
			List<Sample<L, F>> eval = this.getEvalFold(i, FOLD_NB, samples);
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

	private <L, F> List<Sample<L, F>> getEvalFold(int foldIndex, int foldNb, List<Sample<L, F>> samples) {
		List<Sample<L, F>> eval = new ArrayList<Sample<L, F>>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i >= start && i < end) {
				eval.add(samples.get(i));
			}
		}

		return eval;
	}

	private <L, F> List<Sample<L, F>> getTrainingFolds(int foldIndex, int foldNb, List<Sample<L, F>> samples) {
		List<Sample<L, F>> train = new ArrayList<Sample<L, F>>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i < start || i >= end) {
				train.add(samples.get(i));
			}
		}

		return train;
	}
}
