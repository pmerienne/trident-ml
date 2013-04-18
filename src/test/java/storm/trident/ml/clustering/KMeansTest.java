package storm.trident.ml.clustering;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.RandEvaluator;

public class KMeansTest {

	private final static File CLUSTERING_FILE = new File("src/test/resources/clustering-data.csv");
	private final static List<Instance<Integer>> clusteringInstances = new ArrayList<Instance<Integer>>();
	static {
		try {
			loadClusteringData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAgainstGaussianInstance() {
		// Given
		int nbCluster = 5;
		KMeans kMeans = new KMeans(nbCluster);
		List<Instance<Integer>> samples = this.loadGaussianInstances(nbCluster, 500);

		int maxTrainingCount = (int) (samples.size() * 0.80);
		List<Instance<Integer>> training = samples.subList(0, maxTrainingCount);
		List<Instance<Integer>> eval = samples.subList(maxTrainingCount, samples.size());

		// When
		for (Instance<Integer> sample : training) {
			kMeans.update(sample.features);
		}

		// Then
		RandEvaluator randEvaluator = new RandEvaluator();
		double randIndex = randEvaluator.evaluate(kMeans, eval);
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.80);
	}

	@Test
	public void testAgainstRealDataset() {
		// Given
		KMeans kMeans = new KMeans(7);

		int maxTrainingCount = (int) (clusteringInstances.size() * 0.80);
		List<Instance<Integer>> training = clusteringInstances.subList(0, maxTrainingCount);
		List<Instance<Integer>> eval = clusteringInstances.subList(maxTrainingCount, clusteringInstances.size());

		// When
		for (Instance<Integer> sample : training) {
			kMeans.update(sample.features);
		}

		// Then
		RandEvaluator randEvaluator = new RandEvaluator();
		double randIndex = randEvaluator.evaluate(kMeans, eval);
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.70);
	}

	protected List<Instance<Integer>> loadGaussianInstances(int nbCluster, int nbInstances) {
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

	protected static void loadClusteringData() throws IOException {
		FileInputStream is = new FileInputStream(CLUSTERING_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(",");

					Integer label = Integer.parseInt(values[10]);

					double[] features = new double[8];
					for (int i = 1; i < 9; i++) {
						features[i - 1] = Double.parseDouble(values[i]);
					}

					clusteringInstances.add(new Instance<Integer>(label, features));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				Collections.shuffle(clusteringInstances);
			}
		} finally {
			is.close();
			br.close();
		}
	}
}
