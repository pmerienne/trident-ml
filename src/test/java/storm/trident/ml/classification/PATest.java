package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.classification.PAClassifier.Type;
import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.Datasets;

public class PATest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Instance<Boolean>> samples = Datasets.generatedNandInstances(100);
		double error = this.eval(new PAClassifier(), samples);
		assertTrue("Error " + error + " is to big!", error < 0.01);
	}

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new PAClassifier(), Datasets.generateDataForClassification(1000, 10));
		double error1 = this.eval(new PAClassifier(Type.PA1), Datasets.generateDataForClassification(1000, 10));
		double error2 = this.eval(new PAClassifier(Type.PA2), Datasets.generateDataForClassification(1000, 10));

		assertTrue("Error " + error + " is to big!", error <= 0.05);
		assertTrue("Error " + error + " is to big!", error1 <= 0.05);
		assertTrue("Error " + error + " is to big!", error2 <= 0.05);
	}

	@Test
	public void testWithSPAMData() {
		double error = this.eval(new PAClassifier(), Datasets.getSpamSamples());
		double error1 = this.eval(new PAClassifier(Type.PA1), Datasets.getSpamSamples());
		double error2 = this.eval(new PAClassifier(Type.PA2), Datasets.getSpamSamples());
		assertTrue("Error " + error + " is to big!", error <= 0.20);
		assertTrue("Error " + error + " is to big!", error1 <= 0.20);
		assertTrue("Error " + error + " is to big!", error2 <= 0.20);
	}

}
