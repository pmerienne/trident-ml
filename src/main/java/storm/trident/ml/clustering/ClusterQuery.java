package storm.trident.ml.clustering;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class ClusterQuery extends BaseQueryFunction<MapState<Clusterer>, Integer> {

	private static final long serialVersionUID = -2431540558642267325L;

	private String clustererName;

	public ClusterQuery(String clustererName) {
		this.clustererName = clustererName;
	}

	@Override
	public List<Integer> batchRetrieve(MapState<Clusterer> state, List<TridentTuple> tuples) {
		List<Integer> clusterIndexes = new ArrayList<Integer>();

		List<Clusterer> clusterers = state.multiGet(KeysUtil.toKeys(this.clustererName));
		if (clusterers != null && !clusterers.isEmpty()) {
			Clusterer clusterer = clusterers.get(0);

			Integer clustererIndex;
			double[] features;
			for (TridentTuple tuple : tuples) {
				features = this.extractFeatures(tuple);
				clustererIndex = clusterer.classify(features);
				clusterIndexes.add(clustererIndex);
			}
		}

		return clusterIndexes;
	}

	protected double[] extractFeatures(TridentTuple tuple) {
		double[] features = new double[tuple.size()];
		for (int i = 0; i < tuple.size(); i++) {
			features[i] = tuple.getDouble(i);
		}
		return features;
	}

	public void execute(TridentTuple tuple, Integer result, TridentCollector collector) {
		collector.emit(new Values(result));
	}
}
