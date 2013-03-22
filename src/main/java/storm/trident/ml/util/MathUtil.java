package storm.trident.ml.util;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {

	public static Double dotProduct(List<Double> a, List<Double> b) {
		if (a.size() != b.size()) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		Double sum = 0.0;
		for (int i = 0; i < a.size(); i++) {
			sum += a.get(i) * b.get(i);
		}

		return sum;
	}


	public static List<Double> multiply(Double multiplier, List<Double> a) {
		List<Double> results = new ArrayList<Double>(a.size());

		for (Double value : a) {
			results.add(value * multiplier);
		}

		return results;
	}
	
	public static List<Double> multiply(List<Double> a, Double multiplier) {
		List<Double> results = new ArrayList<Double>(a.size());

		for (Double value : a) {
			results.add(value * multiplier);
		}

		return results;
	}

	public static List<Double> add(List<Double> a, List<Double> b) {
		if (a.size() != b.size()) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		List<Double> results = new ArrayList<Double>(a.size());

		for (int i = 0; i < a.size(); i++) {
			results.add(a.get(i) + b.get(i));
		}

		return results;
	}

	public static List<Double> subtract(List<Double> a, List<Double> b) {
		if (a.size() != b.size()) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		List<Double> results = new ArrayList<Double>(a.size());

		for (int i = 0; i < a.size(); i++) {
			results.add(a.get(i) - b.get(i));
		}

		return results;
	}

	public static Double norm(List<Double> a) {
		double meanSqrd = 0;

		for (int i = 0; i < a.size(); i++) {
			meanSqrd += a.get(i) * a.get(i);
		}

		return Math.sqrt(meanSqrd);
	}

	public static Double euclideanDistance(List<Double> a, List<Double> b) {
		if (a.size() != b.size()) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		Double sum = 0.0;

		for (int i = 0; i < a.size(); i++) {
			sum = +Math.pow(a.get(i) - b.get(i), 2);
		}

		return Math.sqrt(sum);
	}
}
