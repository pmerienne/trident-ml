package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.testing.data.Datasets;

public class WinnowTest extends ClassifierTest {

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new WinnowClassifier(), Datasets.generateDataForClassification(100, 10));
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}

	@Test
	public void testWithSPAMData() {
		double error = this.eval(new WinnowClassifier(), Datasets.getSpamSamples());
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}
}
