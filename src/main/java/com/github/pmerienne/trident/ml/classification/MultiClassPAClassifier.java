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
package com.github.pmerienne.trident.ml.classification;

import com.github.pmerienne.trident.ml.util.MathUtil;

public class MultiClassPAClassifier implements Classifier<Integer> {

	private static final long serialVersionUID = -5163481593640555140L;

	private double[][] weightVectors;

	private Type type = Type.STANDARD;
	private Double aggressiveness = 0.001;

	private Integer nbClasses;

	public MultiClassPAClassifier() {
	}

	public MultiClassPAClassifier(Integer nbClasses) {
		this.nbClasses = nbClasses;
	}

	public MultiClassPAClassifier(Integer nbClasses, Type type) {
		this.nbClasses = nbClasses;
		this.type = type;
	}

	public MultiClassPAClassifier(Integer nbClasses, Type type, Double aggressiveness) {
		this.nbClasses = nbClasses;
		this.type = type;
		this.aggressiveness = aggressiveness;
	}

	@Override
	public Integer classify(double[] features) {
		if (this.weightVectors == null) {
			this.initWeightVectors(features.length);
		}

		Integer prediction = null;
		Double highestScore = -Double.MAX_VALUE;

		Double currentClassScore;
		double[] currentWeightVector;
		for (int i = 0; i < this.weightVectors.length; i++) {
			currentWeightVector = this.weightVectors[i];
			currentClassScore = MathUtil.dot(currentWeightVector, features);
			if (currentClassScore > highestScore) {
				prediction = i;
				highestScore = currentClassScore;
			}
		}

		return prediction;
	}

	@Override
	public void update(Integer expectedLabel, double[] features) {
		Integer predictedLabel = this.classify(features);

		// lagrange multiplier
		double loss = 1.0 - (MathUtil.dot(this.weightVectors[expectedLabel], features) - MathUtil.dot(this.weightVectors[predictedLabel], features));
		double tau = 0.0;

		if (Type.STANDARD.equals(this.type)) {
			tau = loss / (1 + 2 * Math.pow(MathUtil.norm(features), 2));
		} else if (Type.PA1.equals(this.type)) {
			tau = Math.min(this.aggressiveness / 2, loss / (2 * Math.pow(MathUtil.norm(features), 2)));
		} else if (Type.PA2.equals(this.type)) {
			tau = 0.5 * (loss / (Math.pow(MathUtil.norm(features), 2) + (1 / (2 * this.aggressiveness))));
		}

		double[] currentWeightVector;
		for (int i = 0; i < this.weightVectors.length; i++) {
			currentWeightVector = this.weightVectors[i];
			if (i != expectedLabel && i != predictedLabel) {
				// No change
			} else if (i == expectedLabel) {
				this.weightVectors[i] = MathUtil.add(currentWeightVector, MathUtil.mult(features, tau));
			} else if (i == predictedLabel) {
				this.weightVectors[i] = MathUtil.subtract(currentWeightVector, MathUtil.mult(features, tau));
			}
		}
	}

	private void initWeightVectors(int featureSize) {
		this.weightVectors = new double[this.nbClasses][featureSize];
		for (int i = 0; i < this.nbClasses; i++) {
			for (int j = 0; j < featureSize; j++) {
				this.weightVectors[i][j] = 0.0;
			}
		}
	}

	@Override
	public void reset() {
		this.weightVectors = null;
	}

	public double[][] getWeightVectors() {
		return weightVectors;
	}

	public void setWeightVectors(double[][] weightVectors) {
		this.weightVectors = weightVectors;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Double getAggressiveness() {
		return aggressiveness;
	}

	public void setAggressiveness(Double aggressiveness) {
		this.aggressiveness = aggressiveness;
	}

	public Integer getNbClasses() {
		return nbClasses;
	}

	public void setNbClasses(Integer nbClasses) {
		this.nbClasses = nbClasses;
	}

	@Override
	public String toString() {
		return "MultiClassPAClassifier [nbClasses=" + nbClasses + ", type=" + type + ", aggressiveness=" + aggressiveness + "]";
	}

	public static enum Type {
		STANDARD, PA1, PA2;
	}
}
