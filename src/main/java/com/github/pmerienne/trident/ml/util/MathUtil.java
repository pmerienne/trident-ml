/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pmerienne.trident.ml.util;

import org.jblas.DoubleMatrix;

public class MathUtil {

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

	public static double[] normalize(double[] vector) {
		double magnitude = magnitude(vector);
		return magnitude != 0 ? mult(vector, 1 / magnitude) : vector;
	}

	public static double magnitude(double[] vector) {
		double magnitude = 0.0;
		for (int i = 0; i < vector.length; i++) {
			magnitude += Math.pow(vector[i], 2);
		}

		return Math.sqrt(magnitude);
	}
}
