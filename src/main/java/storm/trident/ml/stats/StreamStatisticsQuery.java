package storm.trident.ml.stats;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class StreamStatisticsQuery extends BaseQueryFunction<MapState<StreamStatistics>, Double> {

	private static final long serialVersionUID = -8853291509350751320L;

	private String streamName;

	public StreamStatisticsQuery(String streamName) {
		this.streamName = streamName;
	}

	@Override
	public List<Double> batchRetrieve(MapState<StreamStatistics> state, List<TridentTuple> args) {
		List<Double> results = new ArrayList<Double>();

		List<StreamStatistics> statistics = state.multiGet(KeysUtil.toKeys(this.streamName));
		if (!statistics.isEmpty()) {
			// Get stream statistics
			StreamStatistics stat = statistics.get(0);

			for (TridentTuple request : args) {
				Integer featureIndex = this.getIndexFeatureIndex(request);
				QueryType queryType = this.getQueryType(request);
				Double result = this.getValue(stat, featureIndex, queryType);
				results.add(result);
			}

		}

		return results;
	}

	private Double getValue(StreamStatistics stat, Integer featureIndex, QueryType queryType) {
		Double value = null;
		StreamFeatureStatistics featureStatistics = stat.getFeaturesStatistics().get(featureIndex);

		switch (queryType) {
		case COUNT:
			value = featureStatistics.getCount().doubleValue();
			break;
		case MEAN:
			value = featureStatistics.getMean();
			break;
		case STDDEV:
			value = featureStatistics.getStdDev();
			break;
		case VARIANCE:
			value = featureStatistics.getVariance();
			break;
		}

		return value;
	}

	public void execute(TridentTuple tuple, Double result, TridentCollector collector) {
		collector.emit(new Values(result));
	}

	private Integer getIndexFeatureIndex(TridentTuple args) {
		return args.getInteger(0);
	}

	private QueryType getQueryType(TridentTuple args) {
		return QueryType.valueOf(args.getString(1).toUpperCase());
	}

	private enum QueryType {
		COUNT, MEAN, VARIANCE, STDDEV;
	}

	public static class DRPCArgsToStatsQuery extends BaseFunction {

		private static final long serialVersionUID = 5377161963911252948L;

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String[] words = tuple.getString(0).split(" ");
			Integer featureIndex = Integer.parseInt(words[0]);
			String queryType = words[1];
			collector.emit(new Values(featureIndex, queryType));
		}

	}
}
