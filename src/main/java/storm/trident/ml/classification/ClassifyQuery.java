package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class ClassifyQuery<L> extends BaseQueryFunction<MapState<Classifier<L>>, L> {

	private static final long serialVersionUID = -9046858936834644113L;

	private String classifierName;

	public ClassifyQuery(String classifierName) {
		this.classifierName = classifierName;
	}

	@Override
	public List<L> batchRetrieve(MapState<Classifier<L>> state, List<TridentTuple> tuples) {
		List<L> labels = new ArrayList<L>();

		List<Classifier<L>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		if (classifiers != null && !classifiers.isEmpty()) {
			Classifier<L> classifier = classifiers.get(0);

			L label;
			double[] features;
			for (TridentTuple tuple : tuples) {
				features = this.extractFeatures(tuple);
				label = classifier.classify(features);
				labels.add(label);
			}
		}

		return labels;
	}

	protected double[] extractFeatures(TridentTuple tuple) {
		double[] features = new double[tuple.size()];
		for (int i = 0; i < tuple.size(); i++) {
			features[i] = tuple.getDouble(i);
		}
		return features;
	}

	public void execute(TridentTuple tuple, L result, TridentCollector collector) {
		collector.emit(new Values(result));
	}
}
