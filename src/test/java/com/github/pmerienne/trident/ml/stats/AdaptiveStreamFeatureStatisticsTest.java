/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pmerienne.trident.ml.stats;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import com.github.pmerienne.trident.ml.stats.AdaptiveStreamFeatureStatistics;

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
		assertEquals(expectedMean, statistics.getMean(), 0.3);
		assertEquals(stdDev, statistics.getStdDev(), 0.3);
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
		assertEquals(finalMean, statistics.getMean(), 0.35);
		assertEquals(stdDev, statistics.getStdDev(), 0.35);
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
		assertEquals(expectedMean, statistics.getMean(), 0.3);
		assertEquals(finalStdDev, statistics.getStdDev(), 0.3);
	}
}
