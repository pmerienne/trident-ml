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

public class WinnowClassifier implements Classifier<Boolean> {

	private static final long serialVersionUID = -5163481593640555140L;

	private double[] weights;
	public double promotion = 1.5;
	public double demotion = 0.5;
	public double threshold = 1.0;

	public WinnowClassifier() {
	}

	public WinnowClassifier(double promotion, double demotion, double threshold) {
		this.promotion = promotion;
		this.demotion = demotion;
		this.threshold = threshold;
	}

	@Override
	public Boolean classify(double[] features) {
		if (this.weights == null) {
			this.init(features.length);
		}

		Double evaluation = MathUtil.dot(features, this.weights);

		Boolean prediction = evaluation >= this.threshold ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, double[] features) {
		Boolean predictedLabel = this.classify(features);

		// The model is updated only when a mistake is made
		if (!label.equals(predictedLabel)) {

			for (int i = 0; i < features.length; i++) {
				if (features[i] * this.weights[i] > 0) {
					if (predictedLabel) {
						// Demotion step
						this.weights[i] = this.weights[i] * this.demotion;
					} else {
						// Promotion step
						this.weights[i] = this.weights[i] * this.promotion;
					}
				}
			}
		}
	}

	protected void init(int featureSize) {
		// Init weights
		this.weights = new double[featureSize];
		for (int i = 0; i < featureSize; i++) {
			this.weights[i] = this.threshold / featureSize;
		}
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

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getPromotion() {
		return promotion;
	}

	public void setPromotion(double promotion) {
		this.promotion = promotion;
	}

	public double getDemotion() {
		return demotion;
	}

	public void setDemotion(double demotion) {
		this.demotion = demotion;
	}

	@Override
	public String toString() {
		return "WinnowClassifier [promotion=" + promotion + ", demotion=" + demotion + ", threshold=" + threshold + "]";
	}

}
