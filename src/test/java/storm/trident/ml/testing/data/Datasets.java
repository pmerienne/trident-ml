package storm.trident.ml.testing.data;

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

public class Datasets {

	private final static File USPS_FILE = new File("src/test/resources/usps.csv");
	private final static File SPAM_FILE = new File("src/test/resources/spam.csv");

	public final static List<Sample<Boolean, Double>> SPAM_SAMPLES = new ArrayList<Sample<Boolean, Double>>();
	public final static List<Sample<Integer, Double>> USPS_SAMPLES = new ArrayList<Sample<Integer, Double>>();

	static {
		try {
			loadUSPSData();
			loadSPAMData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadUSPSData() throws IOException {
		FileInputStream is = new FileInputStream(USPS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(" ");

					Integer label = Integer.parseInt(values[0]) - 1;
					List<Double> features = new ArrayList<Double>();
					for (int i = 1; i < values.length; i++) {
						features.add(Double.parseDouble(values[i].split(":")[1]));
					}

					USPS_SAMPLES.add(new Sample<Integer, Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped USPS2 sample : " + line);
				}
			}

			Collections.shuffle(USPS_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	private static void loadSPAMData() throws IOException {
		FileInputStream is = new FileInputStream(SPAM_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Boolean label = "1".equals(values[values.length - 1]);
					List<Double> features = new ArrayList<Double>();
					for (int i = 0; i < values.length - 1; i++) {
						double original = Double.parseDouble(values[i]);
						double rescaled = -3.0 + 4.0 / (1 + Math.exp(-(original)));
						features.add(rescaled);
					}

					SPAM_SAMPLES.add(new Sample<Boolean, Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped PML sample : " + line);
				}
			}

			Collections.shuffle(SPAM_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	public static List<Sample<Boolean, Double>> generatedNandSamples(int nb) {
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

	public static List<Sample<Boolean, Double>> generateDataForClassification(int size, int featureSize) {
		Random random = new Random();
		List<Sample<Boolean, Double>> samples = new ArrayList<Sample<Boolean, Double>>();

		for (int i = 0; i < size; i++) {
			Double label = random.nextDouble() > 0.5 ? 1.0 : -1.0;
			List<Double> features = new ArrayList<Double>();
			for (int j = 0; j < featureSize; j++) {
				features.add((j % 2 == 0 ? 1.0 : -1.0) * label + random.nextDouble() - 0.5);
			}
			samples.add(new Sample<Boolean, Double>(label > 0, features));
		}

		return samples;
	}

	public static List<Sample<Integer, Double>> generateDataForMultiLabelClassification(int size, int featureSize, int nbClasses) {
		Random random = new Random();
		List<Sample<Integer, Double>> samples = new ArrayList<Sample<Integer, Double>>();

		for (int i = 0; i < size; i++) {
			Integer label = random.nextInt(nbClasses);
			List<Double> features = new ArrayList<Double>();
			for (int j = 0; j < featureSize; j++) {
				features.add((j % (label + 1) == 0 ? 1.0 : -1.0) + random.nextDouble() - 0.5);
			}
			samples.add(new Sample<Integer, Double>(label, features));
		}

		return samples;
	}

	public static List<Sample<Double, Double>> generateDataForRegression(int size, int featureSize) {
		List<Sample<Double, Double>> samples = new ArrayList<Sample<Double, Double>>();

		Random random = new Random();
		List<Double> factors = new ArrayList<Double>(featureSize);
		for (int i = 0; i < featureSize; i++) {
			factors.add(random.nextDouble() * (1 + random.nextInt(2)));
		}

		for (int i = 0; i < size; i++) {
			double label = 0.0;
			List<Double> features = new ArrayList<Double>();
			for (int j = 0; j < featureSize; j++) {
				double feature = (j % 2 == 0 ? 1.0 : -1.0) * random.nextDouble();
				features.add(feature);
				label += factors.get(j) * feature;
			}

			samples.add(new Sample<Double, Double>(label, features));
		}

		return samples;
	}
}
