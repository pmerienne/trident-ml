package storm.trident.ml.nlp;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.core.TextInstance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class ClassifyTextQuery extends BaseQueryFunction<MapState<TextClassifier>, Integer> {

	private static final long serialVersionUID = -9046858936834644113L;

	private String classifierName;

	public ClassifyTextQuery(String classifierName) {
		this.classifierName = classifierName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> batchRetrieve(MapState<TextClassifier> state, List<TridentTuple> tuples) {
		List<Integer> labels = new ArrayList<Integer>();

		List<TextClassifier> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		if (classifiers != null && !classifiers.isEmpty()) {
			TextClassifier classifier = classifiers.get(0);

			Integer label;
			TextInstance<Integer> instance;
			for (TridentTuple tuple : tuples) {
				instance = (TextInstance<Integer>) tuple.get(0);
				label = classifier.classify(instance.tokens);
				labels.add(label);
			}
		}

		return labels;
	}

	public void execute(TridentTuple tuple, Integer result, TridentCollector collector) {
		collector.emit(new Values(result));
	}

}
