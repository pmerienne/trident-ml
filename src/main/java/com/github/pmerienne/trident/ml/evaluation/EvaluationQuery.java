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

import java.util.ArrayList;
import java.util.List;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

import com.github.pmerienne.trident.ml.util.KeysUtil;

public class EvaluationQuery<L> extends BaseQueryFunction<MapState<Evaluator<L>>, Double> {

	private static final long serialVersionUID = 1L;

	private final String evaluationName;

	public EvaluationQuery(String classifierName) {
		this.evaluationName = classifierName;
	}

	@Override
	public List<Double> batchRetrieve(MapState<Evaluator<L>> state, List<TridentTuple> tuples) {
		Double evaluation = null;

		List<Evaluator<L>> evaluators = state.multiGet(KeysUtil.toKeys(this.evaluationName));
		if (evaluators != null && !evaluators.isEmpty()) {
			Evaluator<L> evaluator = evaluators.get(0);
			if (evaluator != null) {
				evaluation = evaluator.getEvaluation();
			}
		}

		List<Double> evaluations = new ArrayList<Double>(tuples.size());
		for (int i = 0; i < tuples.size(); i++) {
			evaluations.add(evaluation);
		}

		return evaluations;
	}

	@Override
	public void execute(TridentTuple tuple, Double result, TridentCollector collector) {
		collector.emit(new Values(result));
	}

}
