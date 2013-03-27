package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

public class PerceptronClassifier implements Classifier<Boolean, Double> {

	private static final long serialVersionUID = 6891301088355888762L;

	private List<Double> weights;

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
	public void update(Boolean label, List<Double> features) {
		Boolean predictedLanel = this.classify(features);

		if (!label.equals(predictedLanel)) {
			Double error = Boolean.TRUE.equals(label) ? 1.0 : -1.0;

			// Get correction
			Double correction;
			for (int i = 0; i < features.size(); i++) {
				correction = features.get(i) * error * this.learningRate;
				this.weights.set(i, this.weights.get(i) + correction);
			}
		}
	}

	@Override
	public Boolean classify(List<Double> features) {
		if (this.weights == null) {
			this.initWeights(features.size());
		}

		Double evaluation = MathUtil.dotProduct(features, weights) + this.bias;

		Boolean prediction = evaluation > this.threshold ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	protected void initWeights(int size) {
		this.weights = new ArrayList<Double>(size);
		for (int i = 0; i < size; i++) {
			this.weights.add(0.0);
		}
	}

	public List<Double> getWeights() {
		return weights;
	}

	public void setWeights(List<Double> weights) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bias);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(learningRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(threshold);
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
		PerceptronClassifier other = (PerceptronClassifier) obj;
		if (Double.doubleToLongBits(bias) != Double.doubleToLongBits(other.bias))
			return false;
		if (Double.doubleToLongBits(learningRate) != Double.doubleToLongBits(other.learningRate))
			return false;
		if (Double.doubleToLongBits(threshold) != Double.doubleToLongBits(other.threshold))
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
		return "Perceptron [bias=" + bias + ", threshold=" + threshold + ", learningRate=" + learningRate + ", weights=" + weights + "]";
	}

}
