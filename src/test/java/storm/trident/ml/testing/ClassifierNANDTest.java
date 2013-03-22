package storm.trident.ml.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import storm.trident.ml.Classifier;

public abstract class ClassifierNANDTest<F> {

	private Random random = new Random();

	protected Classifier<Boolean, F> classifier;

	public ClassifierNANDTest(Classifier<Boolean, F> classifier) {
		this.classifier = classifier;
	}

	public double error() {
		// When
		for (int i = 0; i < 50; i++) {
			List<Boolean> nandInputs = this.randomNANDInputs();
			Boolean label = this.nand(nandInputs);
			this.classifier.update(label, generateFeatures(nandInputs));
		}

		// Then
		double totalCount = 50.0;
		double errorCount = 0.0;
		for (int i = 0; i < totalCount; i++) {
			List<Boolean> nandInputs = this.randomNANDInputs();
			Boolean expectedLabel = this.nand(nandInputs);
			Boolean actualLabel = classifier.classify(generateFeatures(nandInputs));
			if (!expectedLabel.equals(actualLabel)) {
				errorCount++;
			}
		}

		return errorCount / totalCount;
	}

	protected List<Boolean> randomNANDInputs() {
		return Arrays.asList(random.nextBoolean(), random.nextBoolean());
	}

	protected Boolean nand(List<Boolean> nandInputs) {
		return !(nandInputs.get(0) && nandInputs.get(1));
	}

	protected List<F> generateFeatures(List<Boolean> nandInputs) {
		List<F> features = new ArrayList<F>(nandInputs.size());

		for (Boolean bool : nandInputs) {
			features.add(this.toFeature(bool));
		}

		// Add unnecessary value
		features.add(toFeature(true));

		return features;
	}

	protected abstract F toFeature(Boolean input);
}
