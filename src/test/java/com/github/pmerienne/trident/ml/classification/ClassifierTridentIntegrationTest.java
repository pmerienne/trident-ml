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
package com.github.pmerienne.trident.ml.classification;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.pmerienne.trident.ml.classification.ClassifierUpdater;
import com.github.pmerienne.trident.ml.classification.ClassifyQuery;
import com.github.pmerienne.trident.ml.classification.PerceptronClassifier;
import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.testing.NANDSpout;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.testing.MemoryMapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ClassifierTridentIntegrationTest {

	@Test
	public void testInTopology() throws InterruptedException {
		// Start local cluster
		LocalCluster cluster = new LocalCluster();
		LocalDRPC localDRPC = new LocalDRPC();

		try {
			// Build topology
			TridentTopology toppology = new TridentTopology();

			// Training stream
			TridentState perceptronModel = toppology.newStream("nandsamples", new NANDSpout())
			// update classifier
					.partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new ClassifierUpdater<Boolean>("perceptron", new PerceptronClassifier()));

			// Classification stream
			toppology.newDRPCStream("predict", localDRPC)
			// convert DRPC args to instance
					.each(new Fields("args"), new DRPCArgsToInstance(), new Fields("instance"))

					// Query classifier to classify instance
					.stateQuery(perceptronModel, new Fields("instance"), new ClassifyQuery<Boolean>("perceptron"), new Fields("prediction")).project(new Fields("prediction"));
			cluster.submitTopology(this.getClass().getSimpleName(), new Config(), toppology.build());

			Thread.sleep(4000);

			assertEquals(Boolean.TRUE, extractPrediction(localDRPC.execute("predict", "1.0 0.0 0.0")));
			assertEquals(Boolean.TRUE, extractPrediction(localDRPC.execute("predict", "1.0 0.0 1.0")));
			assertEquals(Boolean.TRUE, extractPrediction(localDRPC.execute("predict", "1.0 1.0 0.0")));
			assertEquals(Boolean.FALSE, extractPrediction(localDRPC.execute("predict", "1.0 1.0 1.0")));
		} finally {
			cluster.shutdown();
			localDRPC.shutdown();
		}
	}

	protected static Boolean extractPrediction(String drpcResult) {
		return Boolean.parseBoolean(drpcResult.replaceAll("\\[", "").replaceAll("\\]", ""));
	}

	public static class DRPCArgsToInstance extends BaseFunction {

		private static final long serialVersionUID = -2932222000448806586L;

		@SuppressWarnings("rawtypes")
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String[] args = tuple.getString(0).split(" ");
			double[] features = new double[args.length];
			for (int i = 0; i < args.length; i++) {
				features[i] = Double.parseDouble(args[i]);
			}

			Instance<?> instance = new Instance(features);
			collector.emit(new Values(instance));
		}
	}

}
