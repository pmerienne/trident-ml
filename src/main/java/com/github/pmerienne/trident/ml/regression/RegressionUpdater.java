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

import java.util.Arrays;
import java.util.List;

import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.util.KeysUtil;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class RegressionUpdater extends BaseStateUpdater<MapState<Regressor>> {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4860370637415723032L;

	private String classifierName;

	private Regressor initialRegressor;

	public RegressionUpdater(String classifierName, Regressor initialRegressor) {
		this.classifierName = classifierName;
		this.initialRegressor = initialRegressor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<Regressor> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<Regressor> regressors = state.multiGet(KeysUtil.toKeys(this.classifierName));
		Regressor regressor = null;
		if (regressors != null && !regressors.isEmpty()) {
			regressor = regressors.get(0);
		}

		// Init it if necessary
		if (regressor == null) {
			regressor = this.initialRegressor;
		}

		// Update model
		Instance<Double> instance;
		for (TridentTuple tuple : tuples) {
			instance = (Instance<Double>) tuple.get(0);
			regressor.update(instance.label, instance.features);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(regressor));
	}

}
