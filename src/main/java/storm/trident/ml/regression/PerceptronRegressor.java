package storm.trident.ml.regression;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

public class PerceptronRegressor implements Regressor {

	private List<Double> weights;

	public double learningRate = 0.1;

	public PerceptronRegressor() {
	}

	public PerceptronRegressor(double learningRate) {
		super();
		this.learningRate = learningRate;
	}

	@Override
	public Double predict(List<Double> features) {
		if (this.weights == null) {
			this.initWeights(features.size());
		}

		Double prediction = MathUtil.dotProduct(this.weights, features);
		return prediction;
	}

	@Override
	public void update(Double expected, List<Double> features) {
		Double prediction = this.predict(features);

		if (!expected.equals(prediction)) {
			Double error = expected - prediction;

			// Get correction
			Double correction;
			for (int i = 0; i < features.size(); i++) {
				correction = features.get(i) * error * this.learningRate;
				this.weights.set(i, this.weights.get(i) + correction);
			}
		}
	}

	protected void initWeights(int size) {
		this.weights = new ArrayList<Double>(size);
		for (int i = 0; i < size; i++) {
			this.weights.add(0.0);
		}
	}

	@Override
	public void reset() {
		this.weights = null;
	}

	public List<Double> getWeights() {
		return weights;
	}

	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(learningRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((weights == null) ? 0 : weights.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PerceptronRegressor other = (PerceptronRegressor) obj;
		if (Double.doubleToLongBits(learningRate) != Double.doubleToLongBits(other.learningRate))
			return false;
		if (weights == null) {
			if (other.weights != null)
				return false;
		} else if (!weights.equals(other.weights))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PerceptronRegressor [learningRate=" + learningRate + "]";
	}

}
