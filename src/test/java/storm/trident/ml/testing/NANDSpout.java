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
package storm.trident.ml.testing;

import java.util.Map;
import java.util.Random;

import storm.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class NANDSpout implements IBatchSpout {

	private static final long serialVersionUID = -3885109681047850872L;

	private int maxBatchSize = 10;

	private Random random = new Random();

	@Override
	public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context) {
	}

	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		for (int i = 0; i < this.maxBatchSize; i++) {
			Instance<Boolean> instance = this.createRandomNAND();
			collector.emit(new Values(instance));
		}
	}

	protected Instance<Boolean> createRandomNAND() {
		Boolean x1 = random.nextBoolean();
		Boolean x2 = random.nextBoolean();
		Boolean label = !(x1 && x2);
		double[] features = new double[] { 1.0, x1 ? 1.0 : 0.0, x2 ? 1.0 : 0.0 };

		return new Instance<Boolean>(label, features);
	}

	protected Double noise(Double value) {
		return value + (random.nextDouble() - 0.5) / 5;
	}

	@Override
	public void ack(long batchId) {

	}

	@Override
	public void close() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getComponentConfiguration() {
		return null;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields("instance");
	}
}
