package storm.trident.ml.preprocessing;

import backtype.storm.tuple.Values;
import storm.trident.ml.Instance;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class InstanceWrapper<L> extends BaseFunction {

	private static final long serialVersionUID = 3312351524410720639L;

	private boolean withLabel = true;

	public InstanceWrapper() {
	}

	public InstanceWrapper(boolean withLabel) {
		this.withLabel = withLabel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Instance<L> instance = null;

		if (this.withLabel) {
			L label = (L) tuple.get(0);
			double[] features = new double[tuple.size() - 1];
			for (int i = 1; i < tuple.size(); i++) {
				features[i - 1] = tuple.getDouble(i);
			}

			instance = new Instance<L>(label, features);
		} else {
			double[] features = new double[tuple.size()];
			for (int i = 0; i < tuple.size(); i++) {
				features[i] = tuple.getDouble(i);
			}

			instance = new Instance<L>(features);
		}

		collector.emit(new Values(instance));

	}

}
