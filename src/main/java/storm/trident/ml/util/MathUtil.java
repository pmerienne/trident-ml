package storm.trident.ml.util;

import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

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

	public static List<Double> dotMatrixProduct(List<Double> a, List<List<Double>> b) {
		int dimension = a.size();
		List<Double> result = new ArrayList<Double>(dimension);

		List<Double> columnVector;
		for (int column = 0; column < dimension; column++) {
			columnVector = new ArrayList<Double>(dimension);
			for (int row = 0; row < dimension; row++) {
				columnVector.add(b.get(row).get(column));
			}

			result.add(dotProduct(a, columnVector));
		}

		return result;
	}

	public static List<Double> dotProductMatrix(List<List<Double>> matrix, List<Double> vector) {
		int dimension = vector.size();
		List<Double> result = new ArrayList<Double>(dimension);

		List<Double> rowVector;
		for (int row = 0; row < dimension; row++) {
			rowVector = matrix.get(row);
			result.add(dotProduct(rowVector, vector));
		}

		return result;
	}

	public static List<Double> multiply(Double multiplier, List<Double> a) {
		List<Double> results = new ArrayList<Double>(a.size());

		for (Double value : a) {
			results.add(value * multiplier);
		}

		return results;
	}

	public static List<List<Double>> multiplyMatrix(Double multiplier, List<List<Double>> matrix) {
		int rows = matrix.size();
		int columns = matrix.get(0).size();

		List<List<Double>> results = new ArrayList<List<Double>>(matrix.size());
		for (int row = 0; row < rows; row++) {
			results.add(new ArrayList<Double>(columns));
			for (int column = 0; column < columns; column++) {
				results.get(row).add(matrix.get(row).get(column) * multiplier);
			}
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

	public static List<List<Double>> subtractMatrix(List<List<Double>> a, List<List<Double>> b) {
		int rows = a.size();
		int columns = a.get(0).size();

		List<List<Double>> results = new ArrayList<List<Double>>(rows);

		for (int row = 0; row < rows; row++) {
			results.add(new ArrayList<Double>(columns));
			for (int column = 0; column < columns; column++) {
				results.get(row).add(a.get(row).get(column) - b.get(row).get(column));
			}
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
			sum += Math.pow(a.get(i) - b.get(i), 2);
		}

		return Math.sqrt(sum);
	}

	public static List<List<Double>> identity(int dimension) {
		List<List<Double>> identity = new ArrayList<List<Double>>();
		for (int i = 0; i < dimension; i++) {
			identity.add(new ArrayList<Double>());
			for (int j = 0; j < dimension; j++) {
				identity.get(i).add(i == j ? 1.0 : 0.0);
			}
		}
		return identity;
	}

	public static List<Double> zeros(int dimension) {
		List<Double> zeros = new ArrayList<Double>(dimension);
		for (int i = 0; i < dimension; i++) {
			zeros.add(0.0);
		}
		return zeros;
	}

	public static boolean isZeros(List<Double> vector) {
		boolean zeroVector = true;
		for (Double element : vector) {
			if (element != 0.0) {
				zeroVector = false;
				break;
			}
		}
		return zeroVector;
	}

	public static boolean isZeros(DoubleMatrix matrix) {
		boolean zeroVector = true;

		for (int i = 0; i < matrix.rows; i++) {
			for (int j = 0; j < matrix.columns; j++) {
				if (matrix.get(i, j) != 0.0) {
					zeroVector = false;
					break;
				}
			}
		}

		return zeroVector;
	}

	public static List<List<Double>> vectorProduct(List<Double> vector1, List<Double> vector2) {
		if (vector1.size() != vector2.size()) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		int dimension = vector1.size();
		List<List<Double>> result = new ArrayList<List<Double>>(dimension);

		for (int row = 0; row < dimension; row++) {
			result.add(new ArrayList<Double>());
			for (int column = 0; column < dimension; column++) {
				result.get(row).add(vector1.get(row) * vector2.get(column));
			}
		}

		return result;
	}

	public static List<List<Double>> matrixProduct(List<List<Double>> matrix1, List<List<Double>> matrix2) {
		int dimension = matrix1.size();

		List<List<Double>> matrix = new ArrayList<List<Double>>(dimension);
		for (int row1 = 0; row1 < dimension; row1++) {
			matrix.add(new ArrayList<Double>());

			List<Double> rowVector = new ArrayList<Double>(dimension);
			for (int column1 = 0; column1 < dimension; column1++) {
				rowVector.add(matrix1.get(row1).get(column1));
			}

			for (int column2 = 0; column2 < dimension; column2++) {
				List<Double> columnVector = new ArrayList<Double>(dimension);
				for (int row2 = 0; row2 < dimension; row2++) {
					columnVector.add(matrix2.get(row2).get(column2));
				}

				matrix.get(row1).add(dotProduct(rowVector, columnVector));
			}

		}

		return matrix;
	}

	public static double dot(double[] vector1, double[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		double sum = 0;
		for (int i = 0; i < vector1.length; i++) {
			sum += vector1[i] * vector2[i];
		}

		return sum;
	}

	public static Double norm(double[] vector) {
		double meanSqrd = 0;

		for (int i = 0; i < vector.length; i++) {
			meanSqrd += vector[i] * vector[i];
		}

		return Math.sqrt(meanSqrd);
	}

	public static double[] mult(double[] vector, double scalar) {
		int length = vector.length;
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = vector[i] * scalar;
		}
		return result;
	}

	public static double[] add(double[] vector1, double[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		double[] result = new double[vector1.length];
		assert vector1.length == vector2.length;
		for (int i = 0; i < vector1.length; i++) {
			result[i] = vector1[i] + vector2[i];
		}

		return result;
	}

	public static double[] subtract(double[] vector1, double[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		double[] result = new double[vector1.length];
		assert vector1.length == vector2.length;
		for (int i = 0; i < vector1.length; i++) {
			result[i] = vector1[i] - vector2[i];
		}

		return result;
	}

	public static double euclideanDistance(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("The dimensions have to be equal!");
		}

		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			sum += Math.pow(a[i] - b[i], 2);
		}

		return Math.sqrt(sum);
	}
}
