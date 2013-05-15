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
package com.github.pmerienne.trident.ml.regression;

import com.github.pmerienne.trident.ml.util.MathUtil;

public class PerceptronRegressor implements Regressor {

	private static final long serialVersionUID = -6289701637173820235L;

	private double[] weights;

	public double learningRate = 0.1;

	public PerceptronRegressor() {
	}

	public PerceptronRegressor(double learningRate) {
		super();
		this.learningRate = learningRate;
	}

	@Override
	public Double predict(double[] features) {
		if (this.weights == null) {
			this.initWeights(features.length);
		}

		Double prediction = MathUtil.dot(this.weights, features);
		return prediction;
	}

	@Override
	public void update(Double expected, double[] features) {
		Double prediction = this.predict(features);

		if (!expected.equals(prediction)) {
			Double error = expected - prediction;

			// Get correction
			Double correction;
			for (int i = 0; i < features.length; i++) {
				correction = features[i] * error * this.learningRate;
				this.weights[i] = this.weights[i] + correction;
			}
		}
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

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	@Override
	public String toString() {
		return "PerceptronRegressor [learningRate=" + learningRate + "]";
	}

}
