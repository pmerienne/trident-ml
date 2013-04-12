package storm.trident.ml.regression;

import storm.trident.ml.util.MathUtil;

public class PerceptronRegressor implements Regressor {

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
