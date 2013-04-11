package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import storm.trident.ml.testing.data.Datasets;
import storm.trident.ml.testing.data.Sample;

public class AROWTest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Sample<Boolean, Double>> samples = Datasets.generatedNandSamples(100);
		double error = this.eval(new AROWClassifier(), samples);
		assertTrue("Error " + error + " is to big!", error < 0.01);
	}

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new AROWClassifier(), Datasets.generateDataForClassification(1000, 10));
		assertTrue("Error " + error + " is to big!", error <= 0.01);
	}

	@Ignore("Too long!")
	@Test
	public void testWithSPAMData() {
		double error = this.eval(new AROWClassifier(), Datasets.SPAM_SAMPLES);
		assertTrue("Error " + error + " is to big!", error <= 0.1);
	}

}
