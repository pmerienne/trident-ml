package storm.trident.ml.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class ClusterUpdater<F> extends BaseStateUpdater<MapState<Clusterer<F>>> {

	private static final long serialVersionUID = -1580744366864902217L;

	private String clustererName;

	private Clusterer<F> initialClusterer;

	public ClusterUpdater(String clustererName, Clusterer<F> initialClusterer) {
		this.clustererName = clustererName;
		this.initialClusterer = initialClusterer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateState(MapState<Clusterer<F>> state, List<TridentTuple> tuples, TridentCollector collector) {
		// Get model
		List<Clusterer<F>> clusterers = state.multiGet(KeysUtil.toKeys(this.clustererName));
		Clusterer<F> clusterer = null;
		if (clusterers != null && !clusterers.isEmpty()) {
			clusterer = clusterers.get(0);
		}

		// Init it if necessary
		if (clusterer == null) {
			clusterer = this.initialClusterer;
		}

		// Update model
		List<F> features;
		for (TridentTuple tuple : tuples) {
			features = this.extractFeatures(tuple);
			clusterer.update(features);
		}

		// Save model
		state.multiPut(KeysUtil.toKeys(this.clustererName), Arrays.asList(clusterer));
	}

	@SuppressWarnings("unchecked")
	private List<F> extractFeatures(TridentTuple tuple) {
		List<F> features = new ArrayList<F>();
		for (int i = 0; i < tuple.size(); i++) {
			features.add((F) tuple.get(i));
		}
		return features;
	}

	public String getClustererName() {
		return clustererName;
	}

	public void setClustererName(String clustererName) {
		this.clustererName = clustererName;
	}

	public Clusterer<F> getInitialClusterer() {
		return initialClusterer;
	}

	public void setInitialClusterer(Clusterer<F> initialClusterer) {
		this.initialClusterer = initialClusterer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clustererName == null) ? 0 : clustererName.hashCode());
		result = prime * result + ((initialClusterer == null) ? 0 : initialClusterer.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterUpdater other = (ClusterUpdater) obj;
		if (clustererName == null) {
			if (other.clustererName != null)
				return false;
		} else if (!clustererName.equals(other.clustererName))
			return false;
		if (initialClusterer == null) {
			if (other.initialClusterer != null)
				return false;
		} else if (!initialClusterer.equals(other.initialClusterer))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClusterUpdater [clustererName=" + clustererName + ", initialClusterer=" + initialClusterer + "]";
	}

}
