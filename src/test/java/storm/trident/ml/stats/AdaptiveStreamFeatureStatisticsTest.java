package storm.trident.ml.stats;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class AdaptiveStreamFeatureStatisticsTest {

	@Test
	public void testUpdate() {
		// Given
		Random random = new Random();
		double expectedMean = 4.0;
		double stdDev = 3.0;
		int size = 10000;

		double[] features = new double[size];
		for (int i = 0; i < size; i++) {
			features[i] = expectedMean + random.nextGaussian() * stdDev;
		}

		// When
		AdaptiveStreamFeatureStatistics statistics = new AdaptiveStreamFeatureStatistics(1000);
		for (int i = 0; i < size; i++) {
			statistics.update(features[i]);
		}

		// Then
		assertEquals(expectedMean, statistics.getMean(), 0.1);
		assertEquals(stdDev, statistics.getStdDev(), 0.1);
	}

	@Test
	public void testUpdateWithMovingMean() {
		// Given
		Random random = new Random();
		int size = 100000;
		double startMean = 3.0;
		double finalMean = 6.0;
		double step = 10 * (finalMean - startMean) / size;
		double stdDev = 3.0;

		double[] features = new double[size];
		for (int i = 0; i < size; i++) {
			double currentMean = Math.min(finalMean, i * step + startMean);
			features[i] = currentMean + random.nextGaussian() * stdDev;
		}

		// When
		AdaptiveStreamFeatureStatistics statistics = new AdaptiveStreamFeatureStatistics(1000);
		for (int i = 0; i < size; i++) {
			statistics.update(features[i]);
		}

		// Then
		assertEquals(finalMean, statistics.getMean(), 0.25);
		assertEquals(stdDev, statistics.getStdDev(), 0.25);
	}

	@Test
	public void testUpdateWithMovingStdDev() {
		// Given
		Random random = new Random();
		int size = 100000;
		double expectedMean = 3.0;
		double startStdDev = 2.0;
		double finalStdDev = 4.0;
		double step = (finalStdDev - startStdDev) / size;

		double[] features = new double[size];
		for (int i = 0; i < size; i++) {
			double currentStdDev = i * step + startStdDev;
			features[i] = expectedMean + random.nextGaussian() * currentStdDev;
		}

		// When
		AdaptiveStreamFeatureStatistics statistics = new AdaptiveStreamFeatureStatistics(1000);
		for (int i = 0; i < size; i++) {
			statistics.update(features[i]);
		}

		// Then
		assertEquals(expectedMean, statistics.getMean(), 0.25);
		assertEquals(finalStdDev, statistics.getStdDev(), 0.25);
	}
}
