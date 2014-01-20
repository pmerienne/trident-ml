package com.github.pmerienne.trident.ml.classification;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
public class ClassifyQueryTest {

	@Test
	public void should_classify_instance_using_classifier() {
		// Given
		Boolean expectedLabel1 = true;
		Boolean expectedLabel2 = false;

		String classifierName = "TestLearner";
		ClassifyQuery<Boolean> query = new ClassifyQuery<Boolean>(classifierName);

		double[] features1 = new double[10];
		double[] features2 = new double[10];
		TridentTuple tuple1 = createMockedInstanceTuple(features1);
		TridentTuple tuple2 = createMockedInstanceTuple(features2);
		List<TridentTuple> tuples = Arrays.asList(tuple1, tuple2);

		Classifier<Boolean> expectedClassifier = mock(Classifier.class);
		given(expectedClassifier.classify(same(features1))).willReturn(expectedLabel1);
		given(expectedClassifier.classify(same(features2))).willReturn(expectedLabel2);

		List<List<Object>> expectedKeys = asList(asList((Object) classifierName));
		MapState<Classifier<Boolean>> state = mock(MapState.class);
		given(state.multiGet(expectedKeys)).willReturn(Arrays.asList(expectedClassifier));

		// When
		List<Boolean> labels = query.batchRetrieve(state, tuples);

		assertEquals(2, labels.size());
		assertTrue(labels.get(0));
		assertFalse(labels.get(1));
	}

	@Test
	public void should_classify_instance_without_classifier() {
		// Given
		String classifierName = "TestLearner";
		ClassifyQuery<Boolean> query = new ClassifyQuery<Boolean>(classifierName);

		double[] features1 = new double[10];
		double[] features2 = new double[10];
		TridentTuple tuple1 = createMockedInstanceTuple(features1);
		TridentTuple tuple2 = createMockedInstanceTuple(features2);
		List<TridentTuple> tuples = Arrays.asList(tuple1, tuple2);

		MapState<Classifier<Boolean>> state = mock(MapState.class);
		given(state.multiGet(asList(asList((Object) classifierName)))).willReturn(EMPTY_LIST);

		// When
		List<Boolean> labels = query.batchRetrieve(state, tuples);

		assertEquals(2, labels.size());
		assertNull(labels.get(0));
		assertNull(labels.get(1));
	}

	private TridentTuple createMockedInstanceTuple(double[] features) {
		Instance<Boolean> instance = new Instance<Boolean>(features);

		TridentTuple tuple = mock(TridentTuple.class);
		given(tuple.get(0)).willReturn(instance);

		return tuple;
	}
}
