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
package com.github.pmerienne.trident.ml.regression;

import java.util.ArrayList;
import java.util.List;

import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.util.KeysUtil;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class RegressionQuery extends
		BaseQueryFunction<MapState<Regressor>, Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 582183815675337782L;
	private String regressorName;

	public RegressionQuery(String regressorName) {
		this.regressorName = regressorName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Double> batchRetrieve(MapState<Regressor> state,
			List<TridentTuple> tuples) {
		List<Double> labels = new ArrayList<Double>();

		List<Regressor> regressors = state.multiGet(KeysUtil
				.toKeys(this.regressorName));
		if (regressors != null && !regressors.isEmpty()) {
			Regressor regressor = regressors.get(0);
			if (regressor == null) {
				for (TridentTuple tuple : tuples) {
					labels.add(null);
				}
			} else {
				Double label;
				Instance<Double> instance;
				for (TridentTuple tuple : tuples) {
					instance = (Instance<Double>) tuple.get(0);
					label = regressor.predict(instance.features);
					labels.add(label);
				}
			}
		}

		return labels;
	}

	public void execute(TridentTuple tuple, Double result,
			TridentCollector collector) {
		collector.emit(new Values(result));
	}

}
