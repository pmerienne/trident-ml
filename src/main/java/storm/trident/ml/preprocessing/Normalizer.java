package storm.trident.ml.preprocessing;

import storm.trident.ml.Instance;
import storm.trident.ml.util.MathUtil;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class Normalizer extends BaseFunction {

	private static final long serialVersionUID = 511416266460297754L;

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance<?> instance = (Instance<?>) tuple.get(0);
		Instance<?> normalizedInstance = this.normalize(instance);
		collector.emit(new Values(normalizedInstance));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Instance<?> normalize(Instance<?> instance) {
		double[] normalizedFeatures = MathUtil.normalize(instance.features);
		Instance<?> normalizedInstance = new Instance(instance.label, normalizedFeatures);
		return normalizedInstance;
	}
}
