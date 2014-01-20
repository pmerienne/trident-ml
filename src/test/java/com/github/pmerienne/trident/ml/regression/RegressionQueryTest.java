package com.github.pmerienne.trident.ml.regression;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

import com.github.pmerienne.trident.ml.core.Instance;

@SuppressWarnings("unchecked")
public class RegressionQueryTest {

	@Test
	public void should_classify_instance_using_classifier() {
		// Given
		Double expectedPrediction1 = 2.5;
		Double expectedPrediction2 = 12.8;

		String regressorName = "TestLearner";
		RegressionQuery query = new RegressionQuery(regressorName);

		double[] features1 = new double[10];
		double[] features2 = new double[10];
		TridentTuple tuple1 = createMockedInstanceTuple(features1);
		TridentTuple tuple2 = createMockedInstanceTuple(features2);
		List<TridentTuple> tuples = Arrays.asList(tuple1, tuple2);

		Regressor expectedRegressor = mock(Regressor.class);
		given(expectedRegressor.predict(same(features1))).willReturn(expectedPrediction1);
		given(expectedRegressor.predict(same(features2))).willReturn(expectedPrediction2);

		List<List<Object>> expectedKeys = asList(asList((Object) regressorName));
		MapState<Regressor> state = mock(MapState.class);
		given(state.multiGet(expectedKeys)).willReturn(Arrays.asList(expectedRegressor));

		// When
		List<Double> actualPredictions = query.batchRetrieve(state, tuples);

		assertEquals(2, actualPredictions.size());
		assertEquals(expectedPrediction1, actualPredictions.get(0));
		assertEquals(expectedPrediction2, actualPredictions.get(1));
	}

	@Test
	public void should_classify_instance_without_classifier() {
		// Given
		String regressorName = "TestLearner";
		RegressionQuery query = new RegressionQuery(regressorName);

		TridentTuple tuple1 = mock(TridentTuple.class);
		TridentTuple tuple2 = mock(TridentTuple.class);
		List<TridentTuple> tuples = Arrays.asList(tuple1, tuple2);

		List<List<Object>> expectedKeys = asList(asList((Object) regressorName));
		MapState<Regressor> state = mock(MapState.class);
		given(state.multiGet(expectedKeys)).willReturn(EMPTY_LIST);

		// When
		List<Double> actualPredictions = query.batchRetrieve(state, tuples);

		assertEquals(2, actualPredictions.size());
		assertNull(actualPredictions.get(0));
		assertNull(actualPredictions.get(1));
	}

	private TridentTuple createMockedInstanceTuple(double[] features) {
		Instance<Boolean> instance = new Instance<Boolean>(features);

		TridentTuple tuple = mock(TridentTuple.class);
		given(tuple.get(0)).willReturn(instance);

		return tuple;
	}
}
