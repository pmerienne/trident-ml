package storm.trident.ml.preprocessing;

import storm.trident.ml.core.Instance;
import storm.trident.ml.stats.StreamFeatureStatistics;
import storm.trident.ml.stats.StreamStatistics;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class StandardScaler extends BaseFunction {

	private static final long serialVersionUID = 1740717206768121351L;

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance<?> instance = (Instance<?>) tuple.get(0);
		StreamStatistics streamStatistics = (StreamStatistics) tuple.get(1);

		Instance<?> standardizedInstance = this.standardize(instance, streamStatistics);
		collector.emit(new Values(standardizedInstance));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Instance<?> standardize(Instance<?> instance, StreamStatistics streamStatistics) {
		// init new features
		int featuresSize = instance.features.length;
		double[] standardizedFeatures = new double[featuresSize];

		// Standardize each feature
		StreamFeatureStatistics featureStatistics;
		for (int i = 0; i < featuresSize; i++) {
			featureStatistics = streamStatistics.getFeaturesStatistics().get(i);
			standardizedFeatures[i] = (instance.features[i] - featureStatistics.getMean()) / featureStatistics.getStdDev();
		}

		Instance<?> standardizedInstance = new Instance(instance.label, standardizedFeatures);
		return standardizedInstance;
	}
}
