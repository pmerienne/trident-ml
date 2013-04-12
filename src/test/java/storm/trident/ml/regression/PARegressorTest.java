package storm.trident.ml.regression;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import storm.trident.ml.testing.data.Datasets;

public class PARegressorTest extends RegressorTest {

	@Test
	public void testWithRandomData() {
		double error = this.eval(new PARegressor(), Datasets.generateDataForRegression(2000, 10));
		assertTrue("Error " + error + " is to big!", error <= 0.01);
	}

	@Test
	public void testWithBirthsData() {
		double error = this.eval(new PARegressor(), Datasets.BIRTHS_SAMPLES);
		assertTrue("Error " + error + " is to big!", error <= 0.01);
	}

}
