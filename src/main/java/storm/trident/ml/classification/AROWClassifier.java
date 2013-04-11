package storm.trident.ml.classification;

import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * Adaptive Regularization of Weight Vectors implementation.
 * 
 * @see http://www.cs.jhu.edu/~mdredze/publications/nips09_arow.pdf
 * @author pmerienne
 * 
 */
public class AROWClassifier implements Classifier<Boolean, Double> {

	private static final long serialVersionUID = 206770369174442259L;

	private double r = 1.0;

	private List<Double> weights;
	private List<List<Double>> variance;

	public AROWClassifier() {
	}

	public AROWClassifier(double r) {
		this.r = r;
	}

	@Override
	public Boolean classify(List<Double> features) {
		if (this.weights == null || this.variance == null) {
			this.init(features.size());
		}

		double evaluation = MathUtil.dotProduct(this.weights, features);
		Boolean prediction = evaluation > 0 ? Boolean.TRUE : Boolean.FALSE;

		return prediction;
	}

	@Override
	public void update(Boolean label, List<Double> features) {
		if (this.weights == null || this.variance == null) {
			this.init(features.size());
		}

		double margin = MathUtil.dotProduct(this.weights, features);

		double labelAsDouble = label ? 1.0 : -1.0;
		if (margin * labelAsDouble < 1) {
			double confidence = MathUtil.dotProduct(features, MathUtil.dotMatrixProduct(features, this.variance));
			double beta = 1 / (confidence + this.r);
			double alpha = Math.max(0, beta * (1 - labelAsDouble * MathUtil.dotProduct(features, this.weights)));
			List<Double> delta = MathUtil.multiply(alpha * labelAsDouble, MathUtil.dotProductMatrix(this.variance, features));

			boolean zeroVector = MathUtil.isZeros(delta);
			if (!zeroVector) {
				this.weights = MathUtil.add(this.weights, delta);

				// Matrix library needed!
				List<Double> sumX = MathUtil.dotProductMatrix(this.variance, features);
				List<List<Double>> sumXX = MathUtil.vectorProduct(sumX, features);
				List<List<Double>> betaSumXX = MathUtil.multiplyMatrix(beta, sumXX);
				List<List<Double>> betaSumXXSum = MathUtil.matrixProduct(betaSumXX, this.variance);
				this.variance = MathUtil.subtractMatrix(this.variance, betaSumXXSum);
			}
		}
	}

	@Override
	public void reset() {
		this.weights = null;
		this.variance = null;

	}

	private void init(int featureSize) {
		// Init weights
		this.weights = MathUtil.zeros(featureSize);

		// Init variance
		this.variance = MathUtil.identity(featureSize);
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public List<Double> getWeights() {
		return weights;
	}

	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}

	public List<List<Double>> getVariance() {
		return variance;
	}

	public void setVariance(List<List<Double>> variance) {
		this.variance = variance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(r);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((variance == null) ? 0 : variance.hashCode());
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
		AROWClassifier other = (AROWClassifier) obj;
		if (Double.doubleToLongBits(r) != Double.doubleToLongBits(other.r))
			return false;
		if (variance == null) {
			if (other.variance != null)
				return false;
		} else if (!variance.equals(other.variance))
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
		return "AROWClassifier [r=" + r + "]";
	}

}
