package storm.trident.ml.nlp;

import java.util.Arrays;
import java.util.List;

import storm.trident.ml.core.TextInstance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class TextClassifierUpdater extends BaseStateUpdater<MapState<TextClassifier>> {

	private static final long serialVersionUID = 1943890181994862536L;

	private String classifierName;
	private TextClassifier initialClassifier;

	public TextClassifierUpdater(String classifierName, TextClassifier initialClassifier) {
		this.classifierName = classifierName;
		this.initialClassifier = initialClassifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<TextClassifier> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<TextClassifier> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		TextClassifier classifier = null;
		if (classifiers != null && !classifiers.isEmpty()) {
			classifier = classifiers.get(0);
		}

		// Init it if necessary
		if (classifier == null) {
			classifier = this.initialClassifier;
		}

		// Update model
		TextInstance<Integer> instance;
		for (TridentTuple tuple : tuples) {
			instance = (TextInstance<Integer>) tuple.get(0);
			classifier.update(instance.label, instance.tokens);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(classifier));
	}

}
