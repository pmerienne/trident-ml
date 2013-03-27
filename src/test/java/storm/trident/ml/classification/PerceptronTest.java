package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.testing.Sample;

public class PerceptronTest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Sample<Boolean, Double>> samples = this.generatedNandSamples(100);
		double error = this.eval(new PerceptronClassifier(), samples, 0.80);
		assertTrue(error < 0.001);
	}

	@Test
	public void testWithDiabetes() {
		double error = this.eval(new PerceptronClassifier(), this.getDiabetesSamples(), 0.80);
		assertTrue(error < 0.05);
	}

}
