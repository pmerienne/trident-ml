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

import com.github.pmerienne.trident.ml.stats.FixedStreamFeatureStatistics;

public class FixedStreamFeatureStatisticsTest {

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
		FixedStreamFeatureStatistics statistics = new FixedStreamFeatureStatistics();
		for (int i = 0; i < size; i++) {
			statistics.update(features[i]);
		}

		// Then
		assertEquals(expectedMean, statistics.getMean(), 0.1);
		assertEquals(stdDev, statistics.getStdDev(), 0.1);
	}

}
