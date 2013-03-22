package storm.trident.ml;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

public class WinnowClassifier implements Classifier<Boolean, Boolean> {

	private static final long serialVersionUID = -5163481593640555140L;

	private List<Double> weights;
	public double step = 2;
	public double threshold = 0.5;
	public boolean autoThreshold = true;

	@Override
	public Boolean classify(List<Boolean> features) {
		if (this.weights == null) {
			this.init(features.size());
		}

		Double evaluation = MathUtil.dotProduct(toDoubles(features), this.weights);

		Boolean prediction = evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, List<Boolean> features) {
		Boolean predictedLabel = this.classify(features);

		if (!label.equals(predictedLabel)) {
			for (int i = 0; i < features.size(); i++) {
				if (features.get(i)) {
					if (predictedLabel) {
						// Elimination step
						this.weights.set(i, this.weights.get(i) / this.step);
					} else {
						// Promotion step
						this.weights.set(i, this.step * this.weights.get(i));
					}
				}
			}
		}
	}

	protected void init(int featureSize) {
		// Init weights
		this.weights = new ArrayList<Double>(featureSize);
		for (int i = 0; i < featureSize; i++) {
			this.weights.add(1.0);
		}

		// Auto-set threshold
		if (this.autoThreshold) {
			this.threshold = featureSize / 2.0;
		}
	}

	public List<Double> getWeights() {
		return weights;
	}

	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}

	public boolean isAutoThreshold() {
		return autoThreshold;
	}

	public void setAutoThreshold(boolean autoThreshold) {
		this.autoThreshold = autoThreshold;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (autoThreshold ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(step);
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
		WinnowClassifier other = (WinnowClassifier) obj;
		if (autoThreshold != other.autoThreshold)
			return false;
		if (Double.doubleToLongBits(step) != Double.doubleToLongBits(other.step))
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
		return "Winnow [threshold=" + threshold + ", step=" + step + ", autoThreshold=" + autoThreshold + ", weights=" + weights + "]";
	}

	public static Double toDouble(Boolean value) {
		return value ? 1.0 : 0.0;
	}

	public static List<Double> toDoubles(List<Boolean> values) {
		List<Double> doubles = new ArrayList<Double>(values.size());
		for (Boolean value : values) {
			doubles.add(toDouble(value));
		}
		return doubles;
	}
}
