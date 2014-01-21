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
