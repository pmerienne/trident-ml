package storm.trident.ml.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import backtype.storm.tuple.Values;

import storm.trident.ml.core.Instance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;

public class StreamStatisticsUpdater extends BaseStateUpdater<MapState<StreamStatistics>> {

	private static final long serialVersionUID = 1740717206768121351L;

	private String streamName;

	private StreamStatistics initialStatitics;

	public StreamStatisticsUpdater() {
	}

	public StreamStatisticsUpdater(String streamName, StreamStatistics initialStatitics) {
		this.streamName = streamName;
		this.initialStatitics = initialStatitics;
	}

	@Override
	public void updateState(MapState<StreamStatistics> state, List<TridentTuple> tuples, TridentCollector collector) {
		StreamStatistics streamStatistics = this.getStreamStatistics(state);
		List<Instance<?>> instances = this.extractInstances(tuples);

		// Update stream statistics
		this.updateStatistics(streamStatistics, instances);

		// Save statistics
		state.multiPut(KeysUtil.toKeys(this.streamName), Arrays.asList(streamStatistics));

		// Emit instance and stats for new stream
		for (Instance<?> instance : instances) {
			collector.emit(new Values(instance, streamStatistics));
		}
	}

	protected List<Instance<?>> extractInstances(List<TridentTuple> tuples) {
		List<Instance<?>> instances = new ArrayList<Instance<?>>();

		Instance<?> instance;
		for (TridentTuple tuple : tuples) {
			instance = (Instance<?>) tuple.get(0);
			instances.add(instance);
		}

		return instances;
	}

	protected void updateStatistics(StreamStatistics streamStatistics, List<Instance<?>> instances) {
		for (Instance<?> instance : instances) {
			streamStatistics.update(instance.features);
		}
	}

	protected StreamStatistics getStreamStatistics(MapState<StreamStatistics> state) {
		List<StreamStatistics> streamStatisticss = state.multiGet(KeysUtil.toKeys(this.streamName));
		StreamStatistics streamStatistics = null;
		if (streamStatisticss != null && !streamStatisticss.isEmpty()) {
			streamStatistics = streamStatisticss.get(0);
		}

		// Init it if necessary
		if (streamStatistics == null) {
			streamStatistics = this.initialStatitics;
		}
		return streamStatistics;
	}

}
