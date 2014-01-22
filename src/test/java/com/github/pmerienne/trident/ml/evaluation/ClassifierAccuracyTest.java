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
package com.github.pmerienne.trident.ml.evaluation;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.pmerienne.trident.ml.evaluation.ClassifierAccuracy;

@RunWith(MockitoJUnitRunner.class)
public class ClassifierAccuracyTest {

	@InjectMocks
	private ClassifierAccuracy<Boolean> evaluator;

	@Test
	public void should_compute_accuracy() {
		// Given
		int totalError = 10;
		int totalCount = 100;

		// When
		for (int i = 0; i < totalCount; i++) {
			boolean prediction = nextBoolean();
			boolean expected = i < totalError ? !prediction : prediction;
			evaluator = evaluator.update(expected, prediction);
		}

		// Then
		assertEquals(0.1, evaluator.getEvaluation(), 10e-9);
	}

}
