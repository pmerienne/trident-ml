package storm.trident.ml.testing;

import java.util.Map;
import java.util.Random;

import storm.trident.ml.core.Instance;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class RandomFeaturesSpout implements IBatchSpout {

	private static final long serialVersionUID = -5293861317274377258L;

	private int maxBatchSize = 10;
	private int featureSize = 10;
	private double variance = 3.0;

	private Random random = new Random();

	public RandomFeaturesSpout() {
	}

	public RandomFeaturesSpout(int featureSize, double variance) {
		this.featureSize = featureSize;
		this.variance = variance;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		for (int i = 0; i < this.maxBatchSize; i++) {
			double[] features = new double[this.featureSize];
			for (int j = 0; j < this.featureSize; j++) {
				features[j] = j + this.random.nextGaussian() * this.variance;
			}
			collector.emit(new Values(new Instance(features)));
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
		return new Fields("instance");
	}

}
