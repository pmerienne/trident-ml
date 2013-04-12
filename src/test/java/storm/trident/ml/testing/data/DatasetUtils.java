package storm.trident.ml.testing.data;

import java.util.ArrayList;
import java.util.List;

public class DatasetUtils {

	public static <L> List<Sample<L>> getEvalFold(int foldIndex, int foldNb, List<Sample<L>> samples) {
		List<Sample<L>> eval = new ArrayList<Sample<L>>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i >= start && i < end) {
				eval.add(samples.get(i));
			}
		}

		return eval;
	}

	public static <L> List<Sample<L>> getTrainingFolds(int foldIndex, int foldNb, List<Sample<L>> samples) {
		List<Sample<L>> train = new ArrayList<Sample<L>>();

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
