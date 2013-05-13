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
package storm.trident.ml.classification;

import storm.trident.ml.util.MathUtil;

/**
 * Passive-Aggresive binary classifier.
 * 
 * @see Online Passive-Aggressive Algorithms
 * 
 *      Koby Crammer, Ofer Dekel, Joseph Keshet, Shai Shalev-Shwartz, Yoram
 *      Singer; 7(Mar):551--585, 2006.
 * @author pmerienne
 * 
 */
public class PAClassifier implements Classifier<Boolean> {

	private static final long serialVersionUID = -5163481593640555140L;

	private double[] weights;

	private Type type = Type.STANDARD;
	private Double aggressiveness = 0.001;

	public PAClassifier() {
	}

	public PAClassifier(Type type) {
		this.type = type;
	}

	public PAClassifier(Type type, Double aggressiveness) {
		this.type = type;
		this.aggressiveness = aggressiveness;
	}

	@Override
	public Boolean classify(double[] features) {
		if (this.weights == null) {
			this.init(features.length);
		}

		Double evaluation = MathUtil.dot(features, this.weights);

		Boolean prediction = evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean expectedLabel, double[] features) {
		if (this.weights == null) {
			this.init(features.length);
		}
		Double expectedLabelAsInt = expectedLabel ? 1.0 : -1.0;

		double loss = Math.max(0.0, 1 - (expectedLabelAsInt * MathUtil.dot(this.weights, features)));
		double update = 0;

		if (Type.STANDARD.equals(this.type)) {
			update = loss / (1 + Math.pow(MathUtil.norm(features), 2));
		} else if (Type.PA1.equals(this.type)) {
			update = Math.min(this.aggressiveness, loss / Math.pow(MathUtil.norm(features), 2));
		} else if (Type.PA2.equals(this.type)) {
			update = loss / (Math.pow(MathUtil.norm(features), 2) + (1.0 / (2 * this.aggressiveness)));
		}

		double[] scaledFeatures = MathUtil.mult(features, update * expectedLabelAsInt);
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

	@Override
	public String toString() {
		return "PAClassifier [type=" + type + ", aggressiveness=" + aggressiveness + "]";
	}

	public static enum Type {
		STANDARD, PA1, PA2;
	}

}
