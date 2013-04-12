package storm.trident.ml.classification;

import java.util.Arrays;
import java.util.List;

import storm.trident.ml.Instance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class ClassifierUpdater<L> extends BaseStateUpdater<MapState<Classifier<L>>> {

	private static final long serialVersionUID = 1943890181994862536L;

	private String classifierName;

	private Classifier<L> initialClassifier;

	public ClassifierUpdater(String classifierName, Classifier<L> initialClassifier) {
		this.classifierName = classifierName;
		this.initialClassifier = initialClassifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<Classifier<L>> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<Classifier<L>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		Classifier<L> classifier = null;
		if (classifiers != null && !classifiers.isEmpty()) {
			classifier = classifiers.get(0);
		}

		// Init it if necessary
		if (classifier == null) {
			classifier = this.initialClassifier;
		}

		// Update model
		Instance<L> instance;
		for (TridentTuple tuple : tuples) {
			instance = (Instance<L>) tuple.get(0);
			classifier.update(instance.label, instance.features);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(classifier));
	}

}
