package storm.trident.ml.preprocessing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.ml.classification.ClassifyQuery;
import storm.trident.ml.stats.StreamStatistics;
import storm.trident.ml.stats.StreamStatisticsQuery;
import storm.trident.ml.testing.RandomFeaturesSpout;
import storm.trident.ml.testing.StringToFeatures;
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

			TridentState originalStreamStatistics = toppology.newStream("originalStream", new RandomFeaturesSpout()).partitionPersist(
					new MemoryMapState.Factory(), new Fields("instance"), new Standardizer("testStream", new StreamStatistics()),
					new Fields("standardizedInstance"));

			TridentState standardizedStreamStatistics = originalStreamStatistics.newValuesStream().partitionPersist(new MemoryMapState.Factory(),
					new Fields("standardizedInstance"), new Standardizer("standardizedStream", new StreamStatistics()));

			toppology.newDRPCStream("queryStats", localDRPC)
					.stateQuery(standardizedStreamStatistics, new StreamStatisticsQuery("standardizedStream"), new Fields("stats"))
					.project(new Fields("stats"));

			cluster.submitTopology("testStandardizer", new Config(), toppology.build());

			Thread.sleep(4000);

			System.out.println(localDRPC.execute("queryStats", ""));

		} finally {
			cluster.shutdown();
			localDRPC.shutdown();
		}
	}
}
