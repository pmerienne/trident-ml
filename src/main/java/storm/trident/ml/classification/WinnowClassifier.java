package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

public class WinnowClassifier implements Classifier<Boolean, Double> {

	private static final long serialVersionUID = -5163481593640555140L;

	private List<Double> weights;
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
	public Boolean classify(List<Double> features) {
		if (this.weights == null) {
			this.init(features.size());
		}

		Double evaluation = MathUtil.dotProduct(features, this.weights);

		Boolean prediction = evaluation >= this.threshold ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, List<Double> features) {
		Boolean predictedLabel = this.classify(features);

		// The model is updated only when a mistake is made
		if (!label.equals(predictedLabel)) {

			for (int i = 0; i < features.size(); i++) {
				if (features.get(i) * this.weights.get(i) > 0) {
					if (predictedLabel) {
						// Demotion step
						this.weights.set(i, this.weights.get(i) * this.demotion);
					} else {
						// Promotion step
						this.weights.set(i, this.weights.get(i) * this.promotion);
					}
				}
			}
		}
	}

	protected void init(int featureSize) {
		// Init weights
		this.weights = new ArrayList<Double>(featureSize);
		for (int i = 0; i < featureSize; i++) {
			this.weights.add(this.threshold / featureSize);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(demotion);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(promotion);
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
		if (Double.doubleToLongBits(demotion) != Double.doubleToLongBits(other.demotion))
			return false;
		if (Double.doubleToLongBits(promotion) != Double.doubleToLongBits(other.promotion))
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
		return "WinnowClassifier [promotion=" + promotion + ", demotion=" + demotion + ", threshold=" + threshold + "]";
	}

}
