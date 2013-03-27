package storm.trident.ml.regression;

import org.junit.Test;

import storm.trident.ml.regression.PARegressor.Type;

public class PerceptronRegressorTest extends RegressorTest {

	@Test
	public void testWithHealthData() {
		double error = this.eval(new PerceptronRegressor(), this.getWineSamples(), 0.80);
//		double error1 = this.eval(new PARegressor(Type.PA1), this.getWineSamples(), 0.80);
//		double error2 = this.eval(new PARegressor(Type.PA2), this.getWineSamples(), 0.80);
		
		System.out.println(error);
//		System.out.println(error1);
//		System.out.println(error2);
	}
}
