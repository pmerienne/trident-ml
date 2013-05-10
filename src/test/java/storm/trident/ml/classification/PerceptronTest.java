package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.Datasets;

public class PerceptronTest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Instance<Boolean>> samples = Datasets.generatedNandInstances(100);
		double error = this.eval(new PerceptronClassifier(), samples);
		assertTrue("Error " + error + " is to big!", error < 0.001);
	}

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new PerceptronClassifier(), Datasets.generateDataForClassification(100, 10));
		assertTrue("Error " + error + " is to big!", error < 0.01);
	}

	@Test
	public void testWithSPAMData() {
		double error = this.eval(new PerceptronClassifier(), Datasets.getSpamSamples());
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}

}
