package storm.trident.ml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.testing.ClassifierNANDTest;

public class WinnowTest {

	@Test
	public void testWithNand() {
		double error = new ClassifierNANDTest<Boolean>(new WinnowClassifier()) {
			@Override
			protected Boolean toFeature(Boolean input) {
				return input;
			}
		}.error();

		assertTrue(error < 0.4);
	}

}
