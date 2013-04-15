package storm.trident.ml.classification;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.ml.Instance;
import storm.trident.ml.testing.NANDSpout;
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

			TridentState perceptronModel = toppology.newStream("nandsamples", new NANDSpout()).partitionPersist(new MemoryMapState.Factory(),
					new Fields("instance"), new ClassifierUpdater<Boolean>("test", new PerceptronClassifier()));

			toppology.newDRPCStream("predict", localDRPC).each(new Fields("args"), new DRPCArgsToInstance(), new Fields("instance"))
					.stateQuery(perceptronModel, new Fields("instance"), new ClassifyQuery<Boolean>("test"), new Fields("prediction"))
					.project(new Fields("prediction"));
			cluster.submitTopology("wordCounter", new Config(), toppology.build());

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
