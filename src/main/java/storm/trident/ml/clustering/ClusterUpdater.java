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
package storm.trident.ml.clustering;

import java.util.Arrays;
import java.util.List;

import storm.trident.ml.core.Instance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class ClusterUpdater extends BaseStateUpdater<MapState<Clusterer>> {

	private static final long serialVersionUID = -1580744366864902217L;

	private String clustererName;

	private Clusterer initialClusterer;

	public ClusterUpdater(String clustererName, Clusterer initialClusterer) {
		this.clustererName = clustererName;
		this.initialClusterer = initialClusterer;
	}

	@Override
	public void updateState(MapState<Clusterer> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<Clusterer> clusterers = state.multiGet(KeysUtil.toKeys(this.clustererName));
		Clusterer clusterer = null;
		if (clusterers != null && !clusterers.isEmpty()) {
			clusterer = clusterers.get(0);
		}

		// Init it if necessary
		if (clusterer == null) {
			clusterer = this.initialClusterer;
		}

		// Update model
		Instance<?> instance;
		for (TridentTuple tuple : tuples) {
			instance = (Instance<?>) tuple.get(0);
			clusterer.update(instance.features);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.clustererName), Arrays.asList(clusterer));
	}

	public String getClustererName() {
		return clustererName;
	}

	public void setClustererName(String clustererName) {
		this.clustererName = clustererName;
	}

	public Clusterer getInitialClusterer() {
		return initialClusterer;
	}

	public void setInitialClusterer(Clusterer initialClusterer) {
		this.initialClusterer = initialClusterer;
	}

	@Override
	public String toString() {
		return "ClusterUpdater [clustererName=" + clustererName + ", initialClusterer=" + initialClusterer + "]";
	}

}
