package storm.trident.ml.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import storm.trident.ml.testing.Sample;

public class ClassifierTest {

	private final static File DIABETES_FILE = new File("src/test/resources/diabetes.csv");
	private final static File USPS_FILE = new File("src/test/resources/usps.csv");

	private final static List<Sample<Boolean, Double>> DIABETES_SAMPLES = new ArrayList<Sample<Boolean, Double>>();
	private final static List<Sample<Integer, Double>> USPS_SAMPLES = new ArrayList<Sample<Integer, Double>>();

	static {
		try {
			loadDiabetesData();
			loadUSPSData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected <L, F> double eval(Classifier<L, F> classifier, List<Sample<L, F>> samples, double trainingPercent) {
		int maxTrainingCount = (int) (samples.size() * trainingPercent);
		List<Sample<L, F>> training = new ArrayList<Sample<L, F>>(samples.subList(0, maxTrainingCount));
		List<Sample<L, F>> eval = new ArrayList<Sample<L, F>>(samples.subList(maxTrainingCount, samples.size()));

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

	protected List<Sample<Boolean, Double>> getDiabetesSamples() {
		return DIABETES_SAMPLES;
	}

	protected List<Sample<Integer, Double>> getUSPSSamples() {
		return USPS_SAMPLES;
	}

	protected List<Sample<Boolean, Double>> generatedNandSamples(int nb) {
		Random random = new Random();

		List<Sample<Boolean, Double>> samples = new ArrayList<Sample<Boolean, Double>>();
		for (int i = 0; i < nb; i++) {
			List<Boolean> nandInputs = Arrays.asList(random.nextBoolean(), random.nextBoolean());
			Boolean label = !(nandInputs.get(0) && nandInputs.get(1));
			List<Double> features = Arrays.asList(1.0, nandInputs.get(0) ? 1.0 : -1.0, nandInputs.get(1) ? 1.0 : -1.0);
			samples.add(new Sample<Boolean, Double>(label, features));
		}

		return samples;
	}

	protected List<Sample<Boolean, Boolean>> generatedBinaryNandSamples(int nb) {
		Random random = new Random();

		List<Sample<Boolean, Boolean>> samples = new ArrayList<Sample<Boolean, Boolean>>();
		for (int i = 0; i < nb; i++) {
			List<Boolean> nandInputs = Arrays.asList(random.nextBoolean(), random.nextBoolean());
			Boolean label = !(nandInputs.get(0) && nandInputs.get(1));
			List<Boolean> features = Arrays.asList(true, nandInputs.get(0), nandInputs.get(1));
			samples.add(new Sample<Boolean, Boolean>(label, features));
		}

		return samples;
	}

	private static void loadUSPSData() throws IOException {
		FileInputStream is = new FileInputStream(USPS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Integer label = Integer.parseInt(values[0]);
					List<Double> features = new ArrayList<Double>();
					for (int i = 1; i < values.length; i++) {
						features.add(Double.parseDouble(values[i]));
					}

					USPS_SAMPLES.add(new Sample<Integer, Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped USPS sample : " + line);
				}
			}

			Collections.shuffle(USPS_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	private static void loadDiabetesData() throws IOException {
		FileInputStream is = new FileInputStream(DIABETES_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Boolean label = values[0].equals("+1");
					List<Double> features = new ArrayList<Double>();
					for (int i = 1; i < values.length; i++) {
						features.add(Double.parseDouble(values[i]));
					}

					if(values.length != 9) {
						System.err.println("Bad sample : " + line);
					}
					
					DIABETES_SAMPLES.add(new Sample<Boolean, Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped diabetes sample : " + line);
				}
			}

			Collections.shuffle(DIABETES_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}
}
