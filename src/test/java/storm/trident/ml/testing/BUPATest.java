package storm.trident.ml.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.trident.ml.Classifier;

public class BUPATest {

	private final static File BUPA_FILE = new File("src/test/resources/bupa.csv");
	private final static List<BUPASample> samples = new ArrayList<BUPASample>();

	protected Classifier<Boolean, Double> classifier;
	protected double trainingPercent = 0.80;

	static {
		try {
			loadBUPAData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BUPATest(Classifier<Boolean, Double> classifier) {
		this.classifier = classifier;
	}
	

	public BUPATest(Classifier<Boolean, Double> classifier, double trainingPercent) {
		this.classifier = classifier;
		this.trainingPercent = trainingPercent;
	}
	

	public double error() {
		int maxTrainingCount = (int) (samples.size() * this.trainingPercent);

		double errorCount = 0.0;
		BUPASample currentSample;
		Boolean actualLabel;
		for (int i = 0; i < samples.size(); i++) {
			currentSample = samples.get(i);
			if (i < maxTrainingCount) {
				this.classifier.update(currentSample.label, currentSample.features);
			} else {
				actualLabel = this.classifier.classify(currentSample.features);
				if (!currentSample.label.equals(actualLabel)) {
					errorCount++;
				}
			}
		}

		return errorCount / samples.size();
	}

	protected static void loadBUPAData() throws IOException {
		FileInputStream is = new FileInputStream(BUPA_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Boolean label = Double.parseDouble(values[5]) > 6.0;
					List<Double> features = new ArrayList<Double>();
					for (int i = 0; i < 5; i++) {
						features.add(Double.parseDouble(values[i]));
					}

					samples.add(new BUPASample(label, features));

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			Collections.shuffle(samples);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static class BUPASample {

		public final Boolean label;
		public final List<Double> features;

		public BUPASample(Boolean label, List<Double> features) {
			this.label = label;
			this.features = features;
		}

	}
}
