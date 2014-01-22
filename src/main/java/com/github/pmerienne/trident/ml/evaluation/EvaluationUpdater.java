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

import static java.util.Arrays.asList;

import java.util.List;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.util.KeysUtil;

public class EvaluationUpdater<L> extends BaseStateUpdater<MapState<Evaluator<L>>> {

	private static final long serialVersionUID = -1487125730400387249L;

	private final String evaluationName;
	private final Evaluator<L> initialEvaluator;

	public EvaluationUpdater(String classifierName, Evaluator<L> initialEvaluator) {
		this.evaluationName = classifierName;
		this.initialEvaluator = initialEvaluator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<Evaluator<L>> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get evaluator
		Evaluator<L> evaluator = getEvaluator(state);

		// Update evaluator
		for (TridentTuple tuple : tuples) {
			Instance<L> instance = (Instance<L>) tuple.getValue(0);
			L prediction = (L) tuple.getValue(1);
			evaluator = evaluator.update(instance.getLabel(), prediction);
		}

		// Save evaluator
		state.multiPut(KeysUtil.toKeys(this.evaluationName), asList(evaluator));

		collector.emit(new Values(evaluator.instanceCount(), evaluator.getEvaluation()));
	}

	private Evaluator<L> getEvaluator(MapState<Evaluator<L>> state) {
		Evaluator<L> evaluator = null;

		List<Evaluator<L>> evaluators = state.multiGet(KeysUtil.toKeys(this.evaluationName));
		if (evaluators != null && !evaluators.isEmpty()) {
			evaluator = evaluators.get(0);
		}

		// Init it if necessary
		if (evaluator == null) {
			evaluator = this.initialEvaluator;
		}

		return evaluator;
	}
}
