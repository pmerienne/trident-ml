package storm.trident.ml.testing.data;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.Instance;

public class DatasetUtils {

	public static <L> List<Instance<L>> getEvalFold(int foldIndex, int foldNb, List<Instance<L>> samples) {
		List<Instance<L>> eval = new ArrayList<Instance<L>>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i >= start && i < end) {
				eval.add(samples.get(i));
			}
		}

		return eval;
	}

	public static <L> List<Instance<L>> getTrainingFolds(int foldIndex, int foldNb, List<Instance<L>> samples) {
		List<Instance<L>> train = new ArrayList<Instance<L>>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i < start || i >= end) {
				train.add(samples.get(i));
			}
		}

		return train;
	}
}
