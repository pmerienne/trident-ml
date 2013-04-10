package storm.trident.ml.testing.data;

import java.util.ArrayList;
import java.util.List;

public class DatasetUtils {

	public static <L, F> List<Sample<L, F>> getEvalFold(int foldIndex, int foldNb, List<Sample<L, F>> samples) {
		List<Sample<L, F>> eval = new ArrayList<Sample<L, F>>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i >= start && i < end) {
				eval.add(samples.get(i));
			}
		}

		return eval;
	}

	public static <L, F> List<Sample<L, F>> getTrainingFolds(int foldIndex, int foldNb, List<Sample<L, F>> samples) {
		List<Sample<L, F>> train = new ArrayList<Sample<L, F>>();

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
