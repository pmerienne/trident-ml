package storm.trident.ml.preprocessing;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.Instance;
import storm.trident.ml.stats.StreamFeatureStatistics;
import storm.trident.ml.stats.StreamStatistics;
import storm.trident.ml.stats.StreamStatisticsUpdater;
import storm.trident.operation.TridentCollector;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class Standardizer extends StreamStatisticsUpdater {

	private static final long serialVersionUID = 1740717206768121351L;

	public Standardizer() {
		super();
	}

	public Standardizer(String streamName, StreamStatistics initialStatitics) {
		super(streamName, initialStatitics);
	}

	@Override
	public void updateState(MapState<StreamStatistics> state, List<TridentTuple> tuples, TridentCollector collector) {
		super.updateState(state, tuples, collector);

		StreamStatistics streamStatistics = this.getStreamStatistics(state);
		List<Instance<?>> instances = this.extractInstances(tuples);

		// Emit standardized instances
		List<Instance<?>> standardizedBatchFeatures = this.standardize(streamStatistics, instances);
		for (Instance<?> instance : standardizedBatchFeatures) {
			collector.emit(new Values(instance));
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private List<Instance<?>> standardize(StreamStatistics streamStatistics, List<Instance<?>> instances) {
		List<Instance<?>> standardizedBatchFeatures = new ArrayList<Instance<?>>();

		// Get all means and std devs
		int featuresSize = instances.get(0).features.length;
		double[] means = new double[featuresSize];
		double[] stdDevs = new double[featuresSize];
		int i = 0;
		for (StreamFeatureStatistics streamFeatureStatistics : streamStatistics.getFeaturesStatistics()) {
			means[i] = streamFeatureStatistics.getMean();
			stdDevs[i] = streamFeatureStatistics.getStdDev();
			i++;
		}

		// Standardize each instances
		Instance standardizedInstance;
		for (Instance<?> instance : instances) {
			standardizedInstance = this.standardize(means, stdDevs, instance);
			standardizedBatchFeatures.add(standardizedInstance);
		}

		return standardizedBatchFeatures;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Instance<?> standardize(double[] means, double[] stdDevs, Instance<?> instance) {
		int featuresSize = means.length;

		double[] standardizedFeatures = new double[featuresSize];
		for (int j = 0; j < instance.features.length; j++) {
			standardizedFeatures[j] = (instance.features[j] - means[j]) / stdDevs[j];
		}

		return new Instance(instance.label, standardizedFeatures);
	}

}
