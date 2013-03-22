package storm.trident.ml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import storm.trident.ml.testing.RandEvaluator;
import storm.trident.ml.testing.Sample;

public class KMeansTest {

	private final static File CLUSTERING_FILE = new File("src/test/resources/clustering-data.csv");
	private final static List<Sample<Integer, Double>> clusteringSamples = new ArrayList<Sample<Integer, Double>>();
	static {
		try {
			loadClusteringData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAgainstRealDataset() {
		// Given
		KMeans kMeans = new KMeans(7);

		int maxTrainingCount = (int) (clusteringSamples.size() * 0.80);
		List<Sample<Integer, Double>> training = clusteringSamples.subList(0, maxTrainingCount);
		List<Sample<Integer, Double>> eval = clusteringSamples.subList(maxTrainingCount, clusteringSamples.size());

		// When
		for (Sample<Integer, Double> sample : training) {
			kMeans.update(sample.features);
		}

		// Then
		RandEvaluator randEvaluator = new RandEvaluator();
		System.out.println(randEvaluator.evaluate(kMeans, eval));
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

					List<Double> features = new ArrayList<Double>();
					for (int i = 1; i < 9; i++) {
						features.add(Double.parseDouble(values[i]));
					}

					clusteringSamples.add(new Sample<Integer, Double>(label, features));
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
