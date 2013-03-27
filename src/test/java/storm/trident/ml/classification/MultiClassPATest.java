package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.classification.MultiClassPAClassifier.Type;

public class MultiClassPATest extends ClassifierTest {

	@Test
	public void testWithUSPS() {
		double actualError = this.eval(new MultiClassPAClassifier(10), this.getUSPSSamples(), 0.80);
		double actualError1 = this.eval(new MultiClassPAClassifier(10, Type.PA1), this.getUSPSSamples(), 0.80);
		double actualError2 = this.eval(new MultiClassPAClassifier(10, Type.PA2), this.getUSPSSamples(), 0.80);

		assertTrue(actualError < 0.35);
		assertTrue(actualError1 < 0.35);
		assertTrue(actualError2 < 0.35);
	}
}
