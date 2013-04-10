package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.testing.data.Datasets;
import storm.trident.ml.testing.data.Sample;

public class PerceptronTest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Sample<Boolean, Double>> samples = Datasets.generatedNandSamples(100);
		double error = this.eval(new PerceptronClassifier(), samples);
		assertTrue("Error " + error + " is to big!", error < 0.001);
	}

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new PerceptronClassifier(), Datasets.generateGaussianData(100, 10));
		assertTrue("Error " + error + " is to big!", error < 0.01);
	}

	@Test
	public void testWithSPAMData() {
		double error = this.eval(new PerceptronClassifier(), Datasets.SPAM_SAMPLES);
		assertTrue("Error " + error + " is to big!", error < 0.25);
	}

}
