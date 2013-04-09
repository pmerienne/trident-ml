package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * Balanced Winnow Classifier
 * 
 * @see http://www.cs.cmu.edu/~vitor/papers/kdd06_final.pdf
 * @author pmerienne
 * 
 */
public class BWinnowClassifier implements Classifier<Boolean, Double> {

	private static final long serialVersionUID = -5163481593640555140L;

	/**
	 * Positive model
	 */
	private List<Double> u;

	/**
	 * Negative model
	 */
	private List<Double> v;

	private double promotion = 1.5;
	private double demotion = 0.5;
	private double threshold = 1.0;

	public BWinnowClassifier() {
	}

	public BWinnowClassifier(double promotion, double demotion, double threshold) {
		this.promotion = promotion;
		this.demotion = demotion;
		this.threshold = threshold;
	}

	@Override
	public Boolean classify(List<Double> features) {
		if (this.u == null || this.v == null) {
			this.init(features.size());
		}

		Double evaluation = MathUtil.dotProduct(features, this.u) - MathUtil.dotProduct(features, this.v) - this.threshold;

		Boolean prediction = evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, List<Double> features) {
		Boolean predictedLabel = this.classify(features);

		// The model is updated only when a mistake is made
		if (!label.equals(predictedLabel)) {

			for (int i = 0; i < features.size(); i++) {
				if (features.get(i) > 0) {
					if (predictedLabel) {
						// Demotion step
						this.u.set(i, this.u.get(i) * this.demotion);
						this.v.set(i, this.v.get(i) * this.promotion);
					} else {
						// Promotion step
						this.u.set(i, this.u.get(i) * this.promotion);
						this.v.set(i, this.v.get(i) * this.demotion);
					}
				}
			}
		}
	}

	protected void init(int featureSize) {
		// Init models
		this.u = new ArrayList<Double>(featureSize);
		this.v = new ArrayList<Double>(featureSize);

		for (int i = 0; i < featureSize; i++) {
			this.u.add(2 * this.threshold / featureSize);
			this.v.add(this.threshold / featureSize);
		}
	}

	public List<Double> getU() {
		return u;
	}

	public void setU(List<Double> u) {
		this.u = u;
	}

	public List<Double> getV() {
		return v;
	}

	public void setV(List<Double> v) {
		this.v = v;
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

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public String toString() {
		return "BWinnowClassifier [promotion=" + promotion + ", demotion=" + demotion + ", threshold=" + threshold + "]";
	}

}
