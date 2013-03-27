package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.classification.PAClassifier.Type;
import storm.trident.ml.testing.Sample;

public class PATest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Sample<Boolean, Double>> samples = this.generatedNandSamples(100);
		double error = this.eval(new PAClassifier(), samples, 0.80);
		assertTrue(error < 0.001);
	}

	@Test
	public void testWithDiabetes() {
		double error = this.eval(new PAClassifier(), this.getDiabetesSamples(), 0.80);
		double error1 = this.eval(new PAClassifier(Type.PA1), this.getDiabetesSamples(), 0.80);
		double error2 = this.eval(new PAClassifier(Type.PA2), this.getDiabetesSamples(), 0.80);

		assertTrue(error <= 0.05);
		assertTrue(error1 <= 0.05);
		assertTrue(error2 <= 0.05);
	}
}
