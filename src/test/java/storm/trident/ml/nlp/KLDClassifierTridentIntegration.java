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
package storm.trident.ml.nlp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.ml.preprocessing.TextInstanceCreator;
import storm.trident.ml.testing.ReutersBatchSpout;
import storm.trident.testing.MemoryMapState;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.tuple.Fields;

public class KLDClassifierTridentIntegration {

	@Test
	public void testInTopology() throws InterruptedException {
		// Start local cluster
		LocalCluster cluster = new LocalCluster();
		LocalDRPC localDRPC = new LocalDRPC();

		try {
			// Build topology
			TridentTopology toppology = new TridentTopology();

			// "Training" stream
			TridentState classifierState = toppology.newStream("reutersData", new ReutersBatchSpout())
			// Transform raw data to text instance
					.each(new Fields("label", "text"), new TextInstanceCreator<Integer>(), new Fields("instance"))

					// Update text classifier
					.partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new TextClassifierUpdater<Integer>("newsClassifier", new KLDClassifier(9)));

			// Classification stream
			toppology.newDRPCStream("classify", localDRPC)
			// Convert DRPC args to text instance
					.each(new Fields("args"), new TextInstanceCreator<Integer>(false), new Fields("instance"))

					// Query classifier with text instance
					.stateQuery(classifierState, new Fields("instance"), new ClassifyTextQuery<Integer>("newsClassifier"), new Fields("prediction")).project(new Fields("prediction"));

			cluster.submitTopology(this.getClass().getSimpleName(), new Config(), toppology.build());
			Thread.sleep(4000);

			// Query with DRPC
			for (Integer realClass : ReutersBatchSpout.REUTEURS_EVAL_SAMPLES.keySet()) {
				Integer prediction = extractPrediction(localDRPC.execute("classify", ReutersBatchSpout.REUTEURS_EVAL_SAMPLES.get(realClass)));
				assertEquals(realClass, prediction);
			}

		} finally {
			cluster.shutdown();
			localDRPC.shutdown();
		}
	}

	protected static Integer extractPrediction(String drpcResult) {
		return Integer.parseInt(drpcResult.replaceAll("\\[", "").replaceAll("\\]", ""));
	}
}
