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

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.core.Instance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class ClusterQuery extends BaseQueryFunction<MapState<Clusterer>, Integer> {

	private static final long serialVersionUID = -2431540558642267325L;

	private String clustererName;

	public ClusterQuery(String clustererName) {
		this.clustererName = clustererName;
	}

	@Override
	public List<Integer> batchRetrieve(MapState<Clusterer> state, List<TridentTuple> tuples) {
		List<Integer> clusterIndexes = new ArrayList<Integer>();

		List<Clusterer> clusterers = state.multiGet(KeysUtil.toKeys(this.clustererName));
		if (clusterers != null && !clusterers.isEmpty()) {
			Clusterer clusterer = clusterers.get(0);

			Integer clustererIndex;
			Instance<?> instance;
			for (TridentTuple tuple : tuples) {
				instance = (Instance<?>) tuple.get(0);
				clustererIndex = clusterer.classify(instance.features);
				clusterIndexes.add(clustererIndex);
			}
		}

		return clusterIndexes;
	}

	public void execute(TridentTuple tuple, Integer result, TridentCollector collector) {
		collector.emit(new Values(result));
	}
}
