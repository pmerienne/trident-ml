package storm.trident.ml.preprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.ml.stats.StreamStatistics;
import storm.trident.ml.stats.StreamStatisticsQuery;
import storm.trident.ml.stats.StreamStatisticsUpdater;
import storm.trident.ml.testing.RandomFeaturesSpout;
import storm.trident.testing.MemoryMapState;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.tuple.Fields;

public class StandardizerIntegrationTest {

	@Test
	public void testInTopology() throws InterruptedException {
		// Start local cluster
		LocalCluster cluster = new LocalCluster();
		LocalDRPC localDRPC = new LocalDRPC();

		try {

			// Build topology
			TridentTopology toppology = new TridentTopology();

			TridentState standardizedStreamStatistics = toppology
					// emit tuples with random features
					.newStream("originalStream", new RandomFeaturesSpout(2, 3.0))
					// Standardize features
					.partitionPersist(new MemoryMapState.Factory(), new Fields("instance"), new Standardizer("testStream", new StreamStatistics()),
							new Fields("standardizedInstance"))
					.newValuesStream()
					// Create/Update standardized stream statistics
					.partitionPersist(new MemoryMapState.Factory(), new Fields("standardizedInstance"),
							new StreamStatisticsUpdater("standardizedStream", new StreamStatistics()));

			toppology
					.newDRPCStream("queryStats", localDRPC)
					// Convert DRPC args to stat query
					.each(new Fields("args"), new StreamStatisticsQuery.DRPCArgsToStatsQuery(), new Fields("featureIndex", "queryType"))
					// Query standardized stream statistics
					.stateQuery(standardizedStreamStatistics, new Fields("featureIndex", "queryType"), new StreamStatisticsQuery("standardizedStream"),
							new Fields("stats")).project(new Fields("stats"));

			cluster.submitTopology("testStandardizer", new Config(), toppology.build());

			Thread.sleep(4000);

			double mean0 = extractDouble(localDRPC.execute("queryStats", "0 mean"));
			double mean1 = extractDouble(localDRPC.execute("queryStats", "1 mean"));
			double variance0 = extractDouble(localDRPC.execute("queryStats", "0 variance"));
			double variance1 = extractDouble(localDRPC.execute("queryStats", "1 variance"));

			assertEquals(0.0, mean0, 0.1);
			assertEquals(0.0, mean1, 0.1);
			assertEquals(1.0, variance0, 0.1);
			assertEquals(1.0, variance1, 0.1);
		} finally {
			cluster.shutdown();
			localDRPC.shutdown();
		}
	}

	protected static double extractDouble(String drpcResult) {
		return Double.parseDouble(drpcResult.replaceAll("\\[", "").replaceAll("\\]", ""));
	}
}
