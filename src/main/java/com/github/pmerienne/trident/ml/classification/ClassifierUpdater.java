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
package com.github.pmerienne.trident.ml.classification;

import java.util.Arrays;
import java.util.List;

import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.util.KeysUtil;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class ClassifierUpdater<L> extends BaseStateUpdater<MapState<Classifier<L>>> {

	private static final long serialVersionUID = 1943890181994862536L;

	private String classifierName;

	private Classifier<L> initialClassifier;

	public ClassifierUpdater(String classifierName, Classifier<L> initialClassifier) {
		this.classifierName = classifierName;
		this.initialClassifier = initialClassifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<Classifier<L>> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<Classifier<L>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		Classifier<L> classifier = null;
		if (classifiers != null && !classifiers.isEmpty()) {
			classifier = classifiers.get(0);
		}

		// Init it if necessary
		if (classifier == null) {
			classifier = this.initialClassifier;
		}

		// Update model
		Instance<L> instance;
		for (TridentTuple tuple : tuples) {
			instance = (Instance<L>) tuple.get(0);
			classifier.update(instance.label, instance.features);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(classifier));
	}

}
