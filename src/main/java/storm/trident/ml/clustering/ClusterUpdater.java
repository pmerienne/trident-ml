package storm.trident.ml.clustering;

import java.util.Arrays;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class ClusterUpdater extends BaseStateUpdater<MapState<Clusterer>> {

	private static final long serialVersionUID = -1580744366864902217L;

	private String clustererName;

	private Clusterer initialClusterer;

	public ClusterUpdater(String clustererName, Clusterer initialClusterer) {
		this.clustererName = clustererName;
		this.initialClusterer = initialClusterer;
	}

	@Override
	public void updateState(MapState<Clusterer> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<Clusterer> clusterers = state.multiGet(KeysUtil.toKeys(this.clustererName));
		Clusterer clusterer = null;
		if (clusterers != null && !clusterers.isEmpty()) {
			clusterer = clusterers.get(0);
		}

		// Init it if necessary
		if (clusterer == null) {
			clusterer = this.initialClusterer;
		}

		// Update model
		double[] features;
		for (TridentTuple tuple : tuples) {
			features = this.extractFeatures(tuple);
			clusterer.update(features);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.clustererName), Arrays.asList(clusterer));
	}

	protected double[] extractFeatures(TridentTuple tuple) {
		double[] features = new double[tuple.size()];
		for (int i = 0; i < tuple.size(); i++) {
			features[i] = tuple.getDouble(i);
		}
		return features;
	}

	public String getClustererName() {
		return clustererName;
	}

	public void setClustererName(String clustererName) {
		this.clustererName = clustererName;
	}

	public Clusterer getInitialClusterer() {
		return initialClusterer;
	}

	public void setInitialClusterer(Clusterer initialClusterer) {
		this.initialClusterer = initialClusterer;
	}

	@Override
	public String toString() {
		return "ClusterUpdater [clustererName=" + clustererName + ", initialClusterer=" + initialClusterer + "]";
	}

}
