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

public class GLASSTest {

	private final static File GLASS_FILE = new File("src/test/resources/glass.csv");
	private final static List<GLASSSample> samples = new ArrayList<GLASSSample>();

	protected Classifier<Integer, Double> classifier;
	protected double trainingPercent = 0.80;

	static {
		try {
			loadGLASSData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GLASSTest(Classifier<Integer, Double> classifier) {
		this.classifier = classifier;
	}

	public double error() {
		int maxTrainingCount = (int) (samples.size() * this.trainingPercent);

		double errorCount = 0.0;
		GLASSSample currentSample;
		Integer actualLabel;
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

	private static void loadGLASSData() throws IOException {
		FileInputStream is = new FileInputStream(GLASS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Integer label = Integer.parseInt(values[0]) - 1;
					List<Double> features = new ArrayList<Double>();
					for (int i = 1; i < 19; i++) {
						features.add(Double.parseDouble(values[i]));
					}

					samples.add(new GLASSSample(label, features));

				} catch (Exception ex) {
					System.out.println("Skipped GLASS sample");
				}
			}

			Collections.shuffle(samples);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static class GLASSSample {

		public final Integer label;
		public final List<Double> features;

		public GLASSSample(Integer label, List<Double> features) {
			this.label = label;
			this.features = features;
		}

	}
}
