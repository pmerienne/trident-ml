package storm.trident.ml.clustering;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.Datasets;

public class KMeansTest extends ClustererTest {

	@Test
	public void testAgainstGaussianInstances() {
		int nbCluster = 5;
		KMeans kMeans = new KMeans(nbCluster);
		List<Instance<Integer>> samples = Datasets.generateDataForClusterization(nbCluster, 5000);

		double randIndex = this.eval(kMeans, samples);
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.80);
	}

	@Test
	public void testAgainstRealDataset() {
		KMeans kMeans = new KMeans(7);
		double randIndex = this.eval(kMeans, Datasets.getClusteringSamples());
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.63);
	}
}
