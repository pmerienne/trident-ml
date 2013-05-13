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
package com.github.pmerienne.trident.ml.testing;

import java.util.Map;
import java.util.Random;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.google.common.base.Function;

public class RandomFeaturesSpout implements IBatchSpout {

	private static final long serialVersionUID = -5293861317274377258L;

	private int maxBatchSize = 10;
	private int featureSize = 2;
	private double variance = 3.0;

	private boolean withLabel = true;

	private final static Function<double[], Boolean> FEATURES_TO_LABEL = new Function<double[], Boolean>() {
		@Override
		public Boolean apply(double[] input) {
			double sum = 0;
			for (int i = 0; i < input.length; i++) {
				sum += input[i];
			}
			return sum > 0;
		}
	};

	private Random random = new Random();

	public RandomFeaturesSpout() {
	}

	public RandomFeaturesSpout(boolean withLabel) {
		this.withLabel = withLabel;
	}

	public RandomFeaturesSpout(int featureSize, double variance) {
		this.featureSize = featureSize;
		this.variance = variance;
	}

	public RandomFeaturesSpout(boolean withLabel, int featureSize, double variance) {
		this.withLabel = withLabel;
		this.featureSize = featureSize;
		this.variance = variance;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context) {
	}

	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		for (int i = 0; i < this.maxBatchSize; i++) {
			Values values = new Values();

			double[] features = new double[this.featureSize];
			for (int j = 0; j < this.featureSize; j++) {
				features[j] = j + this.random.nextGaussian() * this.variance;
			}

			if (this.withLabel) {
				values.add(FEATURES_TO_LABEL.apply(features));
			}

			for (double feature : features) {
				values.add(feature);
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

	public double getVariance() {
		return variance;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	public boolean isWithLabel() {
		return withLabel;
	}

	public void setWithLabel(boolean withLabel) {
		this.withLabel = withLabel;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
