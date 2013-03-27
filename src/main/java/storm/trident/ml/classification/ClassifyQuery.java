package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class ClassifyQuery<L, F> extends BaseQueryFunction<MapState<Classifier<L, F>>, L> {

	private static final long serialVersionUID = -9046858936834644113L;

	private String classifierName;

	public ClassifyQuery(String classifierName) {
		this.classifierName = classifierName;
	}

	@Override
	public List<L> batchRetrieve(MapState<Classifier<L, F>> state, List<TridentTuple> tuples) {
		List<L> labels = new ArrayList<L>();

		List<Classifier<L, F>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		if (classifiers != null && !classifiers.isEmpty()) {
			Classifier<L, F> classifier = classifiers.get(0);

			L label;
			List<F> features;
			for (TridentTuple tuple : tuples) {
				features = this.extractFeatures(tuple);
				label = classifier.classify(features);
				labels.add(label);
			}
		}

		return labels;
	}

	@SuppressWarnings("unchecked")
	protected List<F> extractFeatures(TridentTuple tuple) {
		List<F> features = new ArrayList<F>();
		for (int i = 0; i < tuple.size(); i++) {
			features.add((F) tuple.get(i));
		}
		return features;
	}

	public void execute(TridentTuple tuple, L result, TridentCollector collector) {
		collector.emit(new Values(result));
	}
}
