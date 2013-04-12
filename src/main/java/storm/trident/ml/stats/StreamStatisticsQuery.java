package storm.trident.ml.stats;

import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class StreamStatisticsQuery extends BaseQueryFunction<MapState<StreamStatistics>, StreamStatistics> {

	private static final long serialVersionUID = -8853291509350751320L;

	private String streamName;

	public StreamStatisticsQuery(String streamName) {
		this.streamName = streamName;
	}

	@Override
	public List<StreamStatistics> batchRetrieve(MapState<StreamStatistics> state, List<TridentTuple> args) {
		List<StreamStatistics> statistics = state.multiGet(KeysUtil.toKeys(this.streamName));
		return statistics;
	}

	public void execute(TridentTuple tuple, StreamStatistics result, TridentCollector collector) {
		collector.emit(new Values(result));
	}

}
