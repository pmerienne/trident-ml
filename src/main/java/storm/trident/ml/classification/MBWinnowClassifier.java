package storm.trident.ml.classification;

import storm.trident.ml.util.MathUtil;

/**
 * Modified Balanced Winnow Classifier
 * 
 * @see http://www.cs.cmu.edu/~vitor/papers/kdd06_final.pdf
 * @author pmerienne
 * 
 */
public class MBWinnowClassifier implements Classifier<Boolean> {

	private static final long serialVersionUID = -5163481593640555140L;

	/**
	 * Positive model
	 */
	private double[] u;

	/**
	 * Negative model
	 */
	private double[] v;

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
	public Boolean classify(double[] features) {
		if (this.u == null || this.v == null) {
			this.init(features.length);
		}

		Double score = MathUtil.dot(features, this.u) - MathUtil.dot(features, this.v) - this.threshold;

		Boolean prediction = score >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, double[] features) {
		if (this.u == null || this.v == null) {
			this.init(features.length);
		}

		Double score = MathUtil.dot(features, this.u) - MathUtil.dot(features, this.v) - this.threshold;

		// The model is updated only when a mistake is made
		double yt = label ? 1.0 : -1.0;
		if (score * yt >= this.margin) {
			for (int i = 0; i < features.length; i++) {
				if (features[i] > 0) {
					if (!label) {
						// Promotion step
						this.u[i] = this.u[i] * this.promotion * (1 + features[i]);
						this.v[i] = this.v[i] * this.demotion * (1 - features[i]);
					} else {
						// Demotion step
						this.u[i] = this.u[i] * this.demotion * (1 - features[i]);
						this.v[i] = this.v[i] * this.promotion * (1 + features[i]);
					}
				}
			}
		}
	}

	protected void init(int featureSize) {
		// Init models
		this.u = new double[featureSize];
		this.v = new double[featureSize];

		for (int i = 0; i < featureSize; i++) {
			this.u[i] = 2 * this.threshold / featureSize;
			this.v[i] = this.threshold / featureSize;
		}
	}

	@Override
	public void reset() {
		this.u = null;
		this.v = null;
	}

	public double[] getU() {
		return u;
	}

	public void setU(double[] u) {
		this.u = u;
	}

	public double[] getV() {
		return v;
	}

	public void setV(double[] v) {
		this.v = v;
	}

	public double getMargin() {
		return margin;
	}

	public void setMargin(double margin) {
		this.margin = margin;
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
		return "MBWinnowClassifier [promotion=" + promotion + ", demotion=" + demotion + ", threshold=" + threshold + ", margin=" + margin + "]";
	}

}
