package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * Modified Balanced Winnow Classifier
 * 
 * @see http://www.cs.cmu.edu/~vitor/papers/kdd06_final.pdf
 * @author pmerienne
 * 
 */
public class MBWinnowClassifier implements Classifier<Boolean, Double> {

	private static final long serialVersionUID = -5163481593640555140L;

	/**
	 * Positive model
	 */
	private List<Double> u;

	/**
	 * Negative model
	 */
	private List<Double> v;

	public double promotion = 1.5;
	public double demotion = 0.5;
	public double threshold = 1.0;
	public double margin = 1.0;

	public MBWinnowClassifier() {
	}

	public MBWinnowClassifier(double promotion, double demotion, double threshold, double margin) {
		super();
		this.promotion = promotion;
		this.demotion = demotion;
		this.threshold = threshold;
		this.margin = margin;
	}

	@Override
	public Boolean classify(List<Double> features) {
		if (this.u == null || this.v == null) {
			this.init(features.size());
		}

		Double score = MathUtil.dotProduct(features, this.u) - MathUtil.dotProduct(features, this.v) - this.threshold;

		Boolean prediction = score >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, List<Double> features) {
		if (this.u == null || this.v == null) {
			this.init(features.size());
		}

		Double score = MathUtil.dotProduct(features, this.u) - MathUtil.dotProduct(features, this.v) - this.threshold;

		// The model is updated only when a mistake is made
		double yt = label ? 1.0 : -1.0;
		if (score * yt <= this.margin) {
			for (int i = 0; i < features.size(); i++) {
				if (features.get(i) > 0) {
					if (label) {
						// Promotion step
						this.u.set(i, this.u.get(i) * this.promotion * (1 + features.get(i)));
						this.v.set(i, this.v.get(i) * this.demotion * (1 - features.get(i)));
					} else {
						// Demotion step
						this.u.set(i, this.u.get(i) * this.demotion * (1 - features.get(i)));
						this.v.set(i, this.v.get(i) * this.promotion * (1 + features.get(i)));
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
