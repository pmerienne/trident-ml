package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class ClassifierUpdater<L, F> extends BaseStateUpdater<MapState<Classifier<L, F>>> {

	private static final long serialVersionUID = 1943890181994862536L;

	private String classifierName;

	private Classifier<L, F> initialClassifier;

	public ClassifierUpdater(String classifierName, Classifier<L, F> initialClassifier) {
		this.classifierName = classifierName;
		this.initialClassifier = initialClassifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<Classifier<L, F>> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get perceptron model
		List<Classifier<L, F>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		Classifier<L, F> classifier = null;
		if (classifiers != null && !classifiers.isEmpty()) {
			classifier = classifiers.get(0);
		}

		// Init it if necessary
		if (classifier == null) {
			classifier = this.initialClassifier;
		}

		// Update model
		L label;
		List<F> features;
		for (TridentTuple tuple : tuples) {
			label = this.extractLabel(tuple);
			features = this.extractFeatures(tuple);
			classifier.update(label, features);
		}

		// Save perceptron model
		state.multiPut(KeysUtil.toKeys(this.classifierName), Arrays.asList(classifier));
	}

	@SuppressWarnings("unchecked")
	protected List<F> extractFeatures(TridentTuple tuple) {
		List<F> features = new ArrayList<F>();
		for (int i = 1; i < tuple.size(); i++) {
			features.add((F) tuple.get(i));
		}
		return features;
	}

	@SuppressWarnings("unchecked")
	protected L extractLabel(TridentTuple tuple) {
		L label = (L) tuple.get(0);
		return label;
	}

}
