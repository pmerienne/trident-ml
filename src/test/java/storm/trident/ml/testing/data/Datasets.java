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
	private final static File BIRTHS_FILE = new File("src/test/resources/births.csv");

	public final static List<Sample<Boolean>> SPAM_SAMPLES = new ArrayList<Sample<Boolean>>();
	public final static List<Sample<Integer>> USPS_SAMPLES = new ArrayList<Sample<Integer>>();
	public final static List<Sample<Double>> BIRTHS_SAMPLES = new ArrayList<Sample<Double>>();

	static {
		try {
			loadUSPSData();
			loadSPAMData();
			loadBirthsData();
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
					double[] features = new double[values.length - 1];
					for (int i = 1; i < values.length; i++) {
						features[i - 1] = Double.parseDouble(values[i].split(":")[1]);
					}

					USPS_SAMPLES.add(new Sample<Integer>(label, features));
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

	private static void loadSPAMData() throws IOException {
		FileInputStream is = new FileInputStream(SPAM_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Boolean label = "1".equals(values[values.length - 1]);
					double[] features = new double[values.length - 1];
					for (int i = 0; i < values.length - 1; i++) {
						double original = Double.parseDouble(values[i]);
						double rescaled = -3.0 + 4.0 / (1 + Math.exp(-(original)));
						features[i] = rescaled;
					}

					SPAM_SAMPLES.add(new Sample<Boolean>(label, features));
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

	private static void loadBirthsData() throws IOException {
		FileInputStream is = new FileInputStream(BIRTHS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Double label = Double.parseDouble(values[values.length - 1]);
					double[] features = new double[values.length - 1];
					for (int i = 1; i < values.length - 1; i++) {
						features[i - 1] = Double.parseDouble(values[i]);
					}

					BIRTHS_SAMPLES.add(new Sample<Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped PML sample : " + line);
				}
			}

			Collections.shuffle(BIRTHS_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	public static List<Sample<Boolean>> generatedNandSamples(int nb) {
		Random random = new Random();

		List<Sample<Boolean>> samples = new ArrayList<Sample<Boolean>>();
		for (int i = 0; i < nb; i++) {
			List<Boolean> nandInputs = Arrays.asList(random.nextBoolean(), random.nextBoolean());
			Boolean label = !(nandInputs.get(0) && nandInputs.get(1));
			double[] features = new double[] { 1.0, nandInputs.get(0) ? 1.0 : -1.0, nandInputs.get(1) ? 1.0 : -1.0 };
			samples.add(new Sample<Boolean>(label, features));
		}

		return samples;
	}

	public static List<Sample<Boolean>> generateDataForClassification(int size, int featureSize) {
		Random random = new Random();
		List<Sample<Boolean>> samples = new ArrayList<Sample<Boolean>>();

		for (int i = 0; i < size; i++) {
			Double label = random.nextDouble() > 0.5 ? 1.0 : -1.0;
			double[] features = new double[featureSize + 1];
			for (int j = 0; j < featureSize; j++) {
				features[j] = (j % 2 == 0 ? 1.0 : -1.0) * label + random.nextDouble() - 0.5;
			}
			features[featureSize] = 1.0;
			samples.add(new Sample<Boolean>(label > 0, features));
		}

		return samples;
	}

	public static List<Sample<Integer>> generateDataForMultiLabelClassification(int size, int featureSize, int nbClasses) {
		Random random = new Random();
		List<Sample<Integer>> samples = new ArrayList<Sample<Integer>>();

		for (int i = 0; i < size; i++) {
			Integer label = random.nextInt(nbClasses);
			double[] features = new double[featureSize];
			for (int j = 0; j < featureSize; j++) {
				features[j] = (j % (label + 1) == 0 ? 1.0 : -1.0) + random.nextDouble() - 0.5;
			}
			samples.add(new Sample<Integer>(label, features));
		}

		return samples;
	}

	public static List<Sample<Double>> generateDataForRegression(int size, int featureSize) {
		List<Sample<Double>> samples = new ArrayList<Sample<Double>>();

		Random random = new Random();
		List<Double> factors = new ArrayList<Double>(featureSize);
		for (int i = 0; i < featureSize; i++) {
			factors.add(random.nextDouble() * (1 + random.nextInt(2)));
		}

		for (int i = 0; i < size; i++) {
			double label = 0.0;

			double[] features = new double[featureSize];
			for (int j = 0; j < featureSize; j++) {
				double feature = (j % 2 == 0 ? 1.0 : -1.0) * random.nextDouble();
				features[j] = feature;
				label += factors.get(j) * feature;
			}

			samples.add(new Sample<Double>(label, features));
		}

		return samples;
	}
}
