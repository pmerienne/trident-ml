package storm.trident.ml.testing.data;

import java.util.ArrayList;
import java.util.List;

public class DatasetUtils {

	public static <E> List<E> getEvalFold(int foldIndex, int foldNb, List<E> samples) {
		List<E> eval = new ArrayList<E>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i >= start && i < end) {
				eval.add(samples.get(i));
			}
		}

		return eval;
	}

	public static <E> List<E> getTrainingFolds(int foldIndex, int foldNb, List<E> samples) {
		List<E> train = new ArrayList<E>();

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
