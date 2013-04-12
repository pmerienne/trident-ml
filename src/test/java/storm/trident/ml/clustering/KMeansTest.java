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

import storm.trident.ml.testing.RandEvaluator;
import storm.trident.ml.testing.data.Sample;

public class KMeansTest {

	private final static File CLUSTERING_FILE = new File("src/test/resources/clustering-data.csv");
	private final static List<Sample<Integer>> clusteringSamples = new ArrayList<Sample<Integer>>();
	static {
		try {
			loadClusteringData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAgainstGaussianSample() {
		// Given
		int nbCluster = 5;
		KMeans kMeans = new KMeans(nbCluster);
		List<Sample<Integer>> samples = this.loadGaussianSamples(nbCluster, 500);

		int maxTrainingCount = (int) (samples.size() * 0.80);
		List<Sample<Integer>> training = samples.subList(0, maxTrainingCount);
		List<Sample<Integer>> eval = samples.subList(maxTrainingCount, samples.size());

		// When
		for (Sample<Integer> sample : training) {
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

		int maxTrainingCount = (int) (clusteringSamples.size() * 0.80);
		List<Sample<Integer>> training = clusteringSamples.subList(0, maxTrainingCount);
		List<Sample<Integer>> eval = clusteringSamples.subList(maxTrainingCount, clusteringSamples.size());

		// When
		for (Sample<Integer> sample : training) {
			kMeans.update(sample.features);
		}

		// Then
		RandEvaluator randEvaluator = new RandEvaluator();
		double randIndex = randEvaluator.evaluate(kMeans, eval);
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.70);
	}

	protected List<Sample<Integer>> loadGaussianSamples(int nbCluster, int nbSamples) {
		Random random = new Random();

		List<Sample<Integer>> samples = new ArrayList<Sample<Integer>>();
		for (int i = 0; i < nbSamples; i++) {
			Integer label = random.nextInt(nbCluster);
			double[] features = new double[] { label + random.nextDouble() * 1.25, -label + random.nextDouble() * 1.25, random.nextDouble() };
			Sample<Integer> sample = new Sample<Integer>(label, features);
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

					clusteringSamples.add(new Sample<Integer>(label, features));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				Collections.shuffle(clusteringSamples);
			}
		} finally {
			is.close();
			br.close();
		}
	}
}
