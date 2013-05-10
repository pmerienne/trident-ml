package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.testing.data.Datasets;

public class BWinnowTest extends ClassifierTest {

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new BWinnowClassifier(), Datasets.generateDataForClassification(1000, 10));
		assertTrue("Error " + error + " is to big!", error < 0.01);
	}

	@Test
	public void testWithSPAMData() {
		double error = this.eval(new BWinnowClassifier(), Datasets.getSpamSamples());
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}
}
