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

import java.util.List;
import java.util.Map;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.Datasets;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class RandomFeaturesForClusteringSpout implements IBatchSpout {

	private static final long serialVersionUID = -5293861317274377258L;

	private int maxBatchSize = 10;
	private int featureSize = 3;
	private int nbClasses = 3;

	private boolean withLabel = true;

	public RandomFeaturesForClusteringSpout() {
	}

	public RandomFeaturesForClusteringSpout(boolean withLabel) {
		this.withLabel = withLabel;
	}

	public RandomFeaturesForClusteringSpout(boolean withLabel, int featureSize) {
		this.withLabel = withLabel;
		this.featureSize = featureSize;
	}

	public RandomFeaturesForClusteringSpout(boolean withLabel, int featureSize, int nbClasses) {
		this.withLabel = withLabel;
		this.featureSize = featureSize;
		this.nbClasses = nbClasses;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context) {
	}

	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		List<Instance<Integer>> instances = Datasets.generateDataForMultiLabelClassification(this.maxBatchSize, this.featureSize, this.nbClasses);

		Values values;
		for (Instance<Integer> instance : instances) {
			values = new Values();
			if (this.withLabel) {
				values.add(instance.label);
			}

			for (int i = 0; i < instance.features.length; i++) {
				values.add(instance.features[i]);
			}
			collector.emit(values);
		}
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
		String[] fieldNames;

		if (this.withLabel) {
			fieldNames = new String[this.featureSize + 1];
			fieldNames[0] = "label";
			for (int i = 0; i < this.featureSize; i++) {
				fieldNames[i + 1] = "x" + i;
			}
		} else {
			fieldNames = new String[this.featureSize];
			for (int i = 0; i < this.featureSize; i++) {
				fieldNames[i] = "x" + i;
			}
		}

		return new Fields(fieldNames);
	}

	public int getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public int getFeatureSize() {
		return featureSize;
	}

	public void setFeatureSize(int featureSize) {
		this.featureSize = featureSize;
	}

	public boolean isWithLabel() {
		return withLabel;
	}

	public void setWithLabel(boolean withLabel) {
		this.withLabel = withLabel;
	}

}
