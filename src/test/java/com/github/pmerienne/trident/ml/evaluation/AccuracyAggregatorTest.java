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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.tuple.TridentTuple;

import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.evaluation.AccuracyAggregator.AccuracyState;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AccuracyAggregatorTest {

	@InjectMocks
	private AccuracyAggregator<Boolean> agrgegator;

	@Test
	public void should_init_zero_accuracy() {
		// When
		AccuracyState<Boolean> actualState = agrgegator.zero();

		// Then
		assertThat(actualState).isEqualTo(new AccuracyState<Boolean>(0, 0));
	}

	@Test
	public void should_init_accuracy_with_error() {
		// Given
		Boolean label = true;
		Boolean prediction = false;

		Instance<Boolean> instance = mock(Instance.class);
		when(instance.getLabel()).thenReturn(label);

		TridentTuple tuple = mock(TridentTuple.class);
		when(tuple.getValue(0)).thenReturn(instance);
		when(tuple.getValue(1)).thenReturn(prediction);

		// When
		AccuracyState<Boolean> actualState = agrgegator.init(tuple);

		// Then
		assertThat(actualState).isEqualTo(new AccuracyState<Boolean>(1, 1));
	}

	@Test
	public void should_init_accuracy_without_error() {
		// Given
		Boolean label = true;
		Boolean prediction = true;

		Instance<Boolean> instance = mock(Instance.class);
		when(instance.getLabel()).thenReturn(label);

		TridentTuple tuple = mock(TridentTuple.class);
		when(tuple.getValue(0)).thenReturn(instance);
		when(tuple.getValue(1)).thenReturn(prediction);

		// When
		AccuracyState<Boolean> actualState = agrgegator.init(tuple);

		// Then
		assertThat(actualState).isEqualTo(new AccuracyState<Boolean>(1, 0));
	}

	@Test
	public void should_combine_accuracy() {
		// Given
		AccuracyState<Boolean> first = new AccuracyState<Boolean>(10, 3);
		AccuracyState<Boolean> second = new AccuracyState<Boolean>(15, 13);

		// When
		AccuracyState<Boolean> combined = agrgegator.combine(first, second);

		// Then
		assertThat(combined).isEqualTo(new AccuracyState<Boolean>(25, 16));
	}

	@Test
	public void should_compute_accuracy() {
		// Given
		AccuracyState<Boolean> second = new AccuracyState<Boolean>(250, 25);

		// When
		double evaluation = second.getEvaluation();

		// Then
		assertThat(evaluation).isEqualTo(0.9);
	}
}
