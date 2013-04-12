package storm.trident.ml.stats;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class SimpleStreamFeatureStatisticsTest {

	@Test
	public void testUpdate() {
		// Given
		Random random = new Random();
		double expectedMean = 4.0;
		double stdDev = 6.0;
		int size = 10000;

		double[] features = new double[size];
		for (int i = 0; i < size; i++) {
			features[i] = expectedMean + random.nextGaussian() * stdDev;
		}

		// When
		SimpleStreamFeatureStatistics statistics = new SimpleStreamFeatureStatistics();
		for (int i = 0; i < size; i++) {
			statistics.update(features[i]);
		}

		// Then
		assertEquals(expectedMean, statistics.getMean(), 0.1);
		assertEquals(stdDev, statistics.getStdDev(), 0.1);
	}

}
