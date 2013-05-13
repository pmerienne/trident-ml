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

/**
 * Passive-Aggresive regressor.
 * 
 * @see Online Passive-Aggressive Algorithms
 * 
 *      Koby Crammer, Ofer Dekel, Joseph Keshet, Shai Shalev-Shwartz, Yoram
 *      Singer; 7(Mar):551--585, 2006.
 * @author pmerienne
 * 
 */
public class PARegressor implements Regressor {

	private static final long serialVersionUID = -5163481593640555140L;

	private double[] weights;

	private Double epsilon = 0.01;

	public PARegressor() {
	}

	public PARegressor(Double epsilon) {
		this.epsilon = epsilon;
	}

	@Override
	public Double predict(double[] features) {
		if (this.weights == null) {
			this.init(features.length);
		}

		Double prediction = MathUtil.dot(features, this.weights);
		return prediction;
	}

	@Override
	public void update(Double expected, double[] features) {
		if (this.weights == null) {
			this.init(features.length);
		}

		Double prediction = this.predict(features);

		double sign = expected - prediction > 0 ? 1.0 : -1.0;
		double loss = Math.max(0.0, Math.abs(prediction - expected) - this.epsilon);
		double update = loss / Math.pow(MathUtil.norm(features), 2);

		double[] scaledFeatures = MathUtil.mult(features, update * sign);
		this.weights = MathUtil.add(this.weights, scaledFeatures);
	}

	protected void init(int featureSize) {
		// Init weights
		this.weights = new double[featureSize];
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

	public Double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(Double epsilon) {
		this.epsilon = epsilon;
	}

	@Override
	public String toString() {
		return "PARegressor [epsilon=" + epsilon + "]";
	}

}
