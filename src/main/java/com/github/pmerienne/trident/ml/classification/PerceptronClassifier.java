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

public class PerceptronClassifier implements Classifier<Boolean> {

	private static final long serialVersionUID = 6891301088355888762L;

	private double[] weights;

	public double bias = 0.0;
	public double threshold = 0.5;
	public double learningRate = 0.1;

	public PerceptronClassifier() {
	}

	public PerceptronClassifier(double bias, double threshold, double learningRate) {
		this.bias = bias;
		this.threshold = threshold;
		this.learningRate = learningRate;
	}

	@Override
	public void update(Boolean label, double[] features) {
		Boolean predictedLanel = this.classify(features);

		if (!label.equals(predictedLanel)) {
			Double error = Boolean.TRUE.equals(label) ? 1.0 : -1.0;

			// Get correction
			Double correction;
			for (int i = 0; i < features.length; i++) {
				correction = features[i] * error * this.learningRate;
				this.weights[i] = this.weights[i] + correction;
			}
		}
	}

	@Override
	public Boolean classify(double[] features) {
		if (this.weights == null) {
			this.initWeights(features.length);
		}

		Double evaluation = MathUtil.dot(features, weights) + this.bias;

		Boolean prediction = evaluation > this.threshold ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	protected void initWeights(int size) {
		this.weights = new double[size];
	}

	@Override
	public void reset() {
		this.weights = null;
	}

	public double[] getWeights() {
		return weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	@Override
	public String toString() {
		return "PerceptronClassifier [bias=" + bias + ", threshold=" + threshold + ", learningRate=" + learningRate + "]";
	}

}
