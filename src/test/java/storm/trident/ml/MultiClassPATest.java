package storm.trident.ml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.MultiClassPAClassifier.Type;
import storm.trident.ml.testing.GLASSTest;

public class MultiClassPATest {

	@Test
	public void testWithGlass() {
		double error = new GLASSTest(new MultiClassPAClassifier(7, 18)).error();
		assertTrue(error < 0.2);

		double error1 = new GLASSTest(new MultiClassPAClassifier(7, 18, Type.PA1)).error();
		assertTrue(error1 < 0.2);

		double error2 = new GLASSTest(new MultiClassPAClassifier(7, 18, Type.PA2)).error();
		assertTrue(error2 < 0.2);
	}
}
