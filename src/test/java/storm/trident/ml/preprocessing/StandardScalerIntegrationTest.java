package storm.trident.ml.preprocessing;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.json.simple.JSONValue;
import org.junit.Test;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.ml.stats.StreamFeatureStatisticsQuery;
import storm.trident.ml.stats.StreamStatistics;
import storm.trident.ml.stats.StreamStatisticsUpdater;
import storm.trident.ml.testing.RandomFeaturesSpout;
import storm.trident.testing.MemoryMapState;
import storm.trident.testing.TuplifyArgs;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.tuple.Fields;

public class StandardScalerIntegrationTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testInTopology() throws InterruptedException {
		// Start local cluster
		LocalCluster cluster = new LocalCluster();
		LocalDRPC localDRPC = new LocalDRPC();

		try {

			// Build topology
			TridentTopology toppology = new TridentTopology();

			TridentState scaledStreamStatistics = toppology
			// emit tuples with random features
					.newStream("originalStream", new RandomFeaturesSpout(false, 2, 3.0))

					// Transform trident tupl to instance
					.each(new Fields("x0", "x1"), new InstanceCreator(false), new Fields("instance"))

					// Update original stream statistics
					.partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new StreamStatisticsUpdater("originalStreamStats", new StreamStatistics()),
							new Fields("instance", "originalStreamStats")).newValuesStream()

					// Standardized stream using original stream statistics
					.each(new Fields("instance", "originalStreamStats"), new StandardScaler(), new Fields("scaledInstance"))

					// Update scaled stream statistics
					.partitionPersist(new MemoryMapState.Factory(), new Fields("scaledInstance"), new StreamStatisticsUpdater("scaledStreamStats", new StreamStatistics()));

			toppology.newDRPCStream("queryStats", localDRPC)
					// Convert DRPC args to stat query
					.each(new Fields("args"), new TuplifyArgs(), new Fields("featureIndex", "queryType"))

					// Query scaled stream statistics
					.stateQuery(scaledStreamStatistics, new Fields("featureIndex", "queryType"), new StreamFeatureStatisticsQuery("scaledStreamStats"), new Fields("stats"))
					.project(new Fields("stats"));

			cluster.submitTopology("testStandardScaler", new Config(), toppology.build());

			Thread.sleep(4000);

			double mean0 = extractDouble(localDRPC.execute("queryStats", "[[0, \"MEAN\"]]"));
			double mean1 = extractDouble(localDRPC.execute("queryStats", "[[1, \"MEAN\"]]"));
			double variance0 = extractDouble(localDRPC.execute("queryStats", "[[0, \"VARIANCE\"]]"));
			double variance1 = extractDouble(localDRPC.execute("queryStats", "[[1, \"VARIANCE\"]]"));

			assertEquals(0.0, mean0, 0.1);
			assertEquals(0.0, mean1, 0.1);
			assertEquals(1.0, variance0, 0.1);
			assertEquals(1.0, variance1, 0.1);
		} finally {
			cluster.shutdown();
			localDRPC.shutdown();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static double extractDouble(String drpcResult) {
		List<List<Object>> values = (List) JSONValue.parse(drpcResult);
		Double value = (Double) values.get(0).get(0);
		return value;
	}
}
