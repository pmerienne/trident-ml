package storm.trident.ml.classification;

import storm.trident.ml.util.MathUtil;

/**
 * Balanced Winnow Classifier
 * 
 * @see http://www.cs.cmu.edu/~vitor/papers/kdd06_final.pdf
 * @author pmerienne
 * 
 */
public class BWinnowClassifier implements Classifier<Boolean> {

	private static final long serialVersionUID = -5163481593640555140L;

	/**
	 * Positive model
	 */
	private double[] u;

	/**
	 * Negative model
	 */
	private double[] v;

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
	public Boolean classify(double[] features) {
		if (this.u == null || this.v == null) {
			this.init(features.length);
		}

		Double evaluation = MathUtil.dot(features, this.u) - MathUtil.dot(features, this.v) - this.threshold;

		Boolean prediction = evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean label, double[] features) {
		Boolean predictedLabel = this.classify(features);

		// The model is updated only when a mistake is made
		if (!label.equals(predictedLabel)) {

			for (int i = 0; i < features.length; i++) {
				if (features[i] > 0) {
					if (predictedLabel) {
						// Demotion step
						this.u[i] = this.u[i] * this.demotion;
						this.v[i] = this.v[i] * this.promotion;
					} else {
						// Promotion step
						this.u[i] = this.u[i] * this.promotion;
						this.v[i] = this.v[i] * this.demotion;
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
