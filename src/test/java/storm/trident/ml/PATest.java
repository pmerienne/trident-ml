package storm.trident.ml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.PAClassifier.Type;
import storm.trident.ml.testing.BUPATest;
import storm.trident.ml.testing.ClassifierNANDTest;

public class PATest {

	@Test
	public void testWithNand() {
		double error = new ClassifierNANDTest<Double>(new PAClassifier()) {
			@Override
			protected Double toFeature(Boolean input) {
				return input ? 1.0 : -1.0;
			}
		}.error();

		assertTrue(error < 0.001);
	}

	@Test
	public void testWithBUPA() {
		double error = new BUPATest(new PAClassifier()).error();
		assertTrue(error < 0.1);

		double error1 = new BUPATest(new PAClassifier(Type.PA1)).error();
		assertTrue(error1 < 0.1);

		double error2 = new BUPATest(new PAClassifier(Type.PA2)).error();
		assertTrue(error2 < 0.1);
	}
}
