package storm.trident.ml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.testing.BUPATest;
import storm.trident.ml.testing.ClassifierNANDTest;

public class PerceptronTest {

	@Test
	public void testWithNand() {
		double error = new ClassifierNANDTest<Double>(new PerceptronClassifier()) {
			@Override
			protected Double toFeature(Boolean input) {
				return input ? 1.0 : -1.0;
			}
		}.error();

		assertTrue(error < 0.001);
	}

	@Test
	public void testWithBUPA() {
		double error = new BUPATest(new PerceptronClassifier()).error();
		assertTrue(error < 0.1);
	}

}
