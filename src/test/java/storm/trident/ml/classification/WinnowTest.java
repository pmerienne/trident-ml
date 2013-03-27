package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.testing.Sample;

public class WinnowTest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Sample<Boolean, Boolean>> samples = this.generatedBinaryNandSamples(100);
		double error = this.eval(new WinnowClassifier(), samples, 0.80);
		assertTrue(error <= 0.4);
	}

}
