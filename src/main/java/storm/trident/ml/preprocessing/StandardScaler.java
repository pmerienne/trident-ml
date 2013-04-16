package storm.trident.ml.preprocessing;

import storm.trident.ml.Instance;
import storm.trident.ml.stats.StreamFeatureStatistics;
import storm.trident.ml.stats.StreamStatistics;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class StandardScaler extends BaseFunction {

	private static final long serialVersionUID = 1740717206768121351L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance<?> instance = (Instance<?>) tuple.get(0);
		StreamStatistics streamStatistics = (StreamStatistics) tuple.get(1);

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
		collector.emit(new Values(standardizedInstance));
	}

}
