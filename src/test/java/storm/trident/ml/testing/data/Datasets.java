package storm.trident.ml.testing.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import storm.trident.ml.core.Instance;
import storm.trident.ml.core.TextInstance;
import storm.trident.ml.preprocessing.EnglishTokenizer;
import storm.trident.ml.preprocessing.TwitterTokenizer;

public class Datasets {

	private final static File USPS_FILE = new File("src/test/resources/usps.csv");
	private final static File SPAM_FILE = new File("src/test/resources/spam.csv");
	private final static File BIRTHS_FILE = new File("src/test/resources/births.csv");
	private final static File REUTEURS_FILE = new File("src/test/resources/reuters.csv");
	private final static File CLUSTERING_FILE = new File("src/test/resources/seeds.csv");
	private final static File TWITTER_FILE = new File("src/test/resources/twitter-sentiment.csv");
	private final static File TWITTER_FILE2 = new File("src/test/resources/twitter-sentiment2.csv");
	private final static File REVIEW_FILE = new File("src/test/resources/review-sentiment.csv");

	private static List<Instance<Boolean>> SPAM_SAMPLES;
	private static List<Instance<Integer>> USPS_SAMPLES;
	private static List<Instance<Double>> BIRTHS_SAMPLES;
	private static List<TextInstance<Integer>> REUTERS_SAMPLES;
	private static List<TextInstance<Integer>> TWITTER_SAMPLES;
	private static List<TextInstance<Integer>> TWITTER_SAMPLES2;
	private static List<TextInstance<Integer>> REVIEW_SAMPLES;
	private static List<Instance<Integer>> CUSTERING_SAMPLES;

	public static List<TextInstance<Integer>> getReviewSamples() {
		if (REVIEW_SAMPLES == null) {
			try {
				loadReviewData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return REVIEW_SAMPLES;
	}
	
	public static List<Instance<Boolean>> getSpamSamples() {
		if (SPAM_SAMPLES == null) {
			try {
				loadSPAMData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return SPAM_SAMPLES;
	}

	public static List<Instance<Integer>> getUSPSSamples() {
		if (USPS_SAMPLES == null) {
			try {
				loadUSPSData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return USPS_SAMPLES;
	}

	public static List<Instance<Double>> getBIRTHSSamples() {
		if (BIRTHS_SAMPLES == null) {
			try {
				loadBirthsData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return BIRTHS_SAMPLES;
	}

	public static List<TextInstance<Integer>> getReutersSamples() {
		if (REUTERS_SAMPLES == null) {
			try {
				loadReutersData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return REUTERS_SAMPLES;
	}

	public static List<TextInstance<Integer>> getTwitterSamples() {
		if (TWITTER_SAMPLES == null) {
			try {
				loadTwitterData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return TWITTER_SAMPLES;
	}

	public static List<TextInstance<Integer>> getTwitter2Samples() {
		if (TWITTER_SAMPLES2 == null) {
			try {
				loadTwitter2Data();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return TWITTER_SAMPLES2;
	}

	public static List<Instance<Integer>> getClusteringSamples() {
		if (CUSTERING_SAMPLES == null) {
			try {
				loadClusteringData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return CUSTERING_SAMPLES;
	}

	private static void loadUSPSData() throws IOException {
		USPS_SAMPLES = new ArrayList<Instance<Integer>>();

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

					USPS_SAMPLES.add(new Instance<Integer>(label, features));
				} catch (Exception ex) {
					System.err.println("Skipped USPS sample : " + line);
				}
			}

			Collections.shuffle(USPS_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	private static void loadSPAMData() throws IOException {
		SPAM_SAMPLES = new ArrayList<Instance<Boolean>>();

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
						features[i] = Double.parseDouble(values[i]);
					}

					SPAM_SAMPLES.add(new Instance<Boolean>(label, features));
				} catch (Exception ex) {
					System.err.println("Skipped SPAM sample : " + line);
				}
			}

			Collections.shuffle(SPAM_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	private static void loadBirthsData() throws IOException {
		BIRTHS_SAMPLES = new ArrayList<Instance<Double>>();

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

					BIRTHS_SAMPLES.add(new Instance<Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped BIRTHS sample : " + line);
				}
			}

			Collections.shuffle(BIRTHS_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static void loadReutersData() throws IOException {
		REUTERS_SAMPLES = new ArrayList<TextInstance<Integer>>();

		EnglishTokenizer tokenizer = new EnglishTokenizer();
		Map<String, Integer> topics = new HashMap<String, Integer>();

		FileInputStream is = new FileInputStream(REUTEURS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					// Get class index
					String topic = line.split(",")[0];
					if (!topics.containsKey(topic)) {
						topics.put(topic, topics.size());
					}
					Integer classIndex = topics.get(topic);

					// Get text
					int startIndex = line.indexOf(" - ");
					String text = line.substring(startIndex, line.length() - 1);

					REUTERS_SAMPLES.add(new TextInstance<Integer>(classIndex, tokenizer.tokenize(text)));
				} catch (Exception ex) {
					System.err.println("Skipped Reuters sample because it can't be parsed : " + line);
				}
			}

			Collections.shuffle(REUTERS_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static void loadTwitterData() throws IOException {
		TWITTER_SAMPLES = new ArrayList<TextInstance<Integer>>();
		TwitterTokenizer tokenizer = new TwitterTokenizer(2, 3);

		FileInputStream is = new FileInputStream(TWITTER_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split("\t");

					Integer classIndex = Integer.parseInt(values[0]);
					String text = line.substring(line.indexOf(",") + 1);

					TWITTER_SAMPLES.add(new TextInstance<Integer>(classIndex, tokenizer.tokenize(text)));
				} catch (Exception ex) {
					System.err.println("Skipped twitter sample because it can't be parsed : " + line);
				}
			}

			Collections.shuffle(TWITTER_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static void loadTwitter2Data() throws IOException {
		TWITTER_SAMPLES2 = new ArrayList<TextInstance<Integer>>();
		TwitterTokenizer tokenizer = new TwitterTokenizer();

		FileInputStream is = new FileInputStream(TWITTER_FILE2);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(",");

					Integer classIndex = Integer.parseInt(values[0]);
					String text = line.substring(line.indexOf(",") + 1);

					TWITTER_SAMPLES2.add(new TextInstance<Integer>(classIndex, tokenizer.tokenize(text)));
				} catch (Exception ex) {
					System.err.println("Skipped twitter sample because it can't be parsed : " + line);
				}
			}

			Collections.shuffle(TWITTER_SAMPLES2);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static void loadReviewData() throws IOException {
		REVIEW_SAMPLES = new ArrayList<TextInstance<Integer>>();
		EnglishTokenizer tokenizer = new EnglishTokenizer(2, 2);

		FileInputStream is = new FileInputStream(REVIEW_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(",");

					Integer classIndex = Integer.parseInt(values[0]);
					String text = line.substring(line.indexOf(",") + 1);

					REVIEW_SAMPLES.add(new TextInstance<Integer>(classIndex, tokenizer.tokenize(text)));
				} catch (Exception ex) {
					System.err.println("Skipped review sample because it can't be parsed : " + line);
				}
			}

			Collections.shuffle(REVIEW_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

	protected static void loadClusteringData() throws IOException {
		CUSTERING_SAMPLES = new ArrayList<Instance<Integer>>();

		FileInputStream is = new FileInputStream(CLUSTERING_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Integer label = Integer.parseInt(values[7]);

					double[] features = new double[values.length - 1];
					for (int i = 0; i < values.length - 1; i++) {
						features[i] = Double.parseDouble(values[i]);
					}

					CUSTERING_SAMPLES.add(new Instance<Integer>(label, features));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				Collections.shuffle(CUSTERING_SAMPLES);
			}
		} finally {
			is.close();
			br.close();
		}
	}

	public static List<Instance<Integer>> generateDataForClusterization(int nbCluster, int nbInstances) {
		Random random = new Random();

		List<Instance<Integer>> samples = new ArrayList<Instance<Integer>>();
		for (int i = 0; i < nbInstances; i++) {
			Integer label = random.nextInt(nbCluster);
			double[] features = new double[] { label + random.nextDouble() * 1.25, -label + random.nextDouble() * 1.25, random.nextDouble() };
			Instance<Integer> sample = new Instance<Integer>(label, features);
			samples.add(sample);
		}

		return samples;
	}

	public static List<Instance<Boolean>> generatedNandInstances(int nb) {
		Random random = new Random();

		List<Instance<Boolean>> samples = new ArrayList<Instance<Boolean>>();
		for (int i = 0; i < nb; i++) {
			List<Boolean> nandInputs = Arrays.asList(random.nextBoolean(), random.nextBoolean());
			Boolean label = !(nandInputs.get(0) && nandInputs.get(1));
			double[] features = new double[] { 1.0, nandInputs.get(0) ? 1.0 : -1.0, nandInputs.get(1) ? 1.0 : -1.0 };
			samples.add(new Instance<Boolean>(label, features));
		}

		return samples;
	}

	public static List<Instance<Boolean>> generateDataForClassification(int size, int featureSize) {
		Random random = new Random();
		List<Instance<Boolean>> samples = new ArrayList<Instance<Boolean>>();

		for (int i = 0; i < size; i++) {
			Double label = random.nextDouble() > 0.5 ? 1.0 : -1.0;
			double[] features = new double[featureSize + 1];
			for (int j = 0; j < featureSize; j++) {
				features[j] = (j % 2 == 0 ? 1.0 : -1.0) * label + random.nextDouble() - 0.5;
			}
			features[featureSize] = 1.0;
			samples.add(new Instance<Boolean>(label > 0, features));
		}

		return samples;
	}

	public static List<Instance<Boolean>> generateNonSeparatableDataForClassification(int size) {
		Random random = new Random();
		List<Instance<Boolean>> samples = new ArrayList<Instance<Boolean>>();

		for (int i = 0; i < size; i++) {
			Boolean label = random.nextDouble() > 0.5;
			double[] features = new double[3];
			features[0] = 1.0;
			features[1] = (label ? -1.0 : 1.0) * random.nextDouble() + random.nextGaussian() / 2;
			features[2] = (label ? -1.0 : 1.0) * random.nextDouble() + random.nextGaussian() / 2;
			samples.add(new Instance<Boolean>(label, features));
		}

		return samples;
	}

	public static List<Instance<Integer>> generateDataForMultiLabelClassification(int size, int featureSize, int nbClasses) {
		Random random = new Random();
		List<Instance<Integer>> samples = new ArrayList<Instance<Integer>>();

		for (int i = 0; i < size; i++) {
			Integer label = random.nextInt(nbClasses);
			double[] features = new double[featureSize];
			for (int j = 0; j < featureSize; j++) {
				features[j] = (j % (label + 1) == 0 ? 1.0 : -1.0) + random.nextDouble() - 0.5;
			}
			samples.add(new Instance<Integer>(label, features));
		}

		return samples;
	}

	public static List<Instance<Double>> generateDataForRegression(int size, int featureSize) {
		List<Instance<Double>> samples = new ArrayList<Instance<Double>>();

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

			samples.add(new Instance<Double>(label, features));
		}

		return samples;
	}
}
