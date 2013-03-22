package storm.trident.ml.testing;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class StringToFeatures extends BaseFunction {

	private static final long serialVersionUID = 2612334438864990496L;

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		Values features = new Values();

		String args = tuple.getString(0);
		for (String word : args.split(" ")) {
			try {
				features.add(Double.parseDouble(word));
			} catch (Exception ex) {
				// Do nothing
			}
		}

		collector.emit(features);
	}

}
