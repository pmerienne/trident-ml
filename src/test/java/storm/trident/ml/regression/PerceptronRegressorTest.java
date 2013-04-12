package storm.trident.ml.regression;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.testing.data.Datasets;

public class PerceptronRegressorTest extends RegressorTest {

	@Test
	public void testWithRandomData() {
		double error = this.eval(new PerceptronRegressor(), Datasets.generateDataForRegression(2000, 10));
		assertTrue("Error " + error + " is to big!", error <= 0.001);
	}

	@Test
	public void testWithBirthsData() {
		double error = this.eval(new PerceptronRegressor(), Datasets.BIRTHS_SAMPLES);
		assertTrue("Error " + error + " is to big!", error <= 0.01);
	}
}
