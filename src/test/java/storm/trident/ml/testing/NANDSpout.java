package storm.trident.ml.testing;

import java.util.Map;
import java.util.Random;

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
			Values values = this.createRandomNAND();
			collector.emit(values);
		}
	}

	protected Values createRandomNAND() {
		Boolean x1 = random.nextBoolean();
		Boolean x2 = random.nextBoolean();
		Boolean label = !(x1 && x2);

		Values sample = new Values(label, 1.0, x1 ? 1.0 : 0.0, x2 ? 1.0 : 0.0);
		return sample;
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
		return new Fields("label", "x0", "x1", "x2");
	}
}
