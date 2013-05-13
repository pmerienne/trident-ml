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
package com.github.pmerienne.trident.ml.preprocessing;

import com.github.pmerienne.trident.ml.core.Instance;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class InstanceCreator<L> extends BaseFunction {

	private static final long serialVersionUID = 3312351524410720639L;

	private boolean withLabel = true;

	public InstanceCreator() {
	}

	public InstanceCreator(boolean withLabel) {
		this.withLabel = withLabel;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance<L> instance = this.createInstance(tuple);
		collector.emit(new Values(instance));
	}

	@SuppressWarnings("unchecked")
	protected Instance<L> createInstance(TridentTuple tuple) {
		Instance<L> instance = null;

		if (this.withLabel) {
			L label = (L) tuple.get(0);
			double[] features = new double[tuple.size() - 1];
			for (int i = 1; i < tuple.size(); i++) {
				features[i - 1] = tuple.getDouble(i);
			}

			instance = new Instance<L>(label, features);
		} else {
			double[] features = new double[tuple.size()];
			for (int i = 0; i < tuple.size(); i++) {
				features[i] = tuple.getDouble(i);
			}

			instance = new Instance<L>(features);
		}

		return instance;
	}
}
