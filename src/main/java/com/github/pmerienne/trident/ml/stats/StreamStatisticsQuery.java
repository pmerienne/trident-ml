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
package com.github.pmerienne.trident.ml.stats;

import java.util.List;

import com.github.pmerienne.trident.ml.util.KeysUtil;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class StreamStatisticsQuery extends BaseQueryFunction<MapState<StreamStatistics>, StreamStatistics> {

	private static final long serialVersionUID = -8853291509350751320L;

	private String streamName;

	public StreamStatisticsQuery(String streamName) {
		this.streamName = streamName;
	}

	@Override
	public List<StreamStatistics> batchRetrieve(MapState<StreamStatistics> state, List<TridentTuple> args) {
		List<StreamStatistics> statistics = state.multiGet(KeysUtil.toKeys(this.streamName));
		return statistics;
	}

	public void execute(TridentTuple tuple, StreamStatistics result, TridentCollector collector) {
		collector.emit(new Values(result));
	}
}
