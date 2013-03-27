package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * Passive-Aggresive binary classifier.
 * 
 * @see Online Passive-Aggressive Algorithms
 * 
 *      Koby Crammer, Ofer Dekel, Joseph Keshet, Shai Shalev-Shwartz, Yoram Singer; 7(Mar):551--585, 2006.
 * @author pmerienne
 * 
 */
public class PAClassifier implements Classifier<Boolean, Double> {

	private static final long serialVersionUID = -5163481593640555140L;

	private List<Double> weights;

	private Type type = Type.STANDARD;
	private Double aggressiveness = 0.001;

	public PAClassifier() {
	}

	public PAClassifier(Type type) {
		this.type = type;
	}

	public PAClassifier(Type type, Double aggressiveness) {
		this.type = type;
		this.aggressiveness = aggressiveness;
	}

	@Override
	public Boolean classify(List<Double> features) {
		if (this.weights == null) {
			this.init(features.size());
		}

		Double evaluation = MathUtil.dotProduct(features, this.weights);

		Boolean prediction = evaluation >= 0 ? Boolean.TRUE : Boolean.FALSE;
		return prediction;
	}

	@Override
	public void update(Boolean expectedLabel, List<Double> features) {
		if (this.weights == null) {
			this.init(features.size());
		}
		Double expectedLabelAsInt = expectedLabel ? 1.0 : -1.0;

		double loss = Math.max(0.0, 1 - (expectedLabelAsInt * MathUtil.dotProduct(this.weights, features)));
		double update = 0;

		if (Type.STANDARD.equals(this.type)) {
			update = loss / Math.pow(MathUtil.norm(features), 2);
		} else if (Type.PA1.equals(this.type)) {
			update = Math.min(this.aggressiveness, loss / Math.pow(MathUtil.norm(features), 2));
		} else if (Type.PA2.equals(this.type)) {
			update = loss / (Math.pow(MathUtil.norm(features), 2) + (1.0 / (2 * this.aggressiveness)));
		}

		List<Double> scaledFeatures = MathUtil.multiply(features, update * expectedLabelAsInt);
		this.weights = MathUtil.add(this.weights, scaledFeatures);
	}

	protected void init(int featureSize) {
		// Init weights
		this.weights = new ArrayList<Double>(featureSize);
		for (int i = 0; i < featureSize; i++) {
			this.weights.add(0.0);
		}
	}

	public List<Double> getWeights() {
		return weights;
	}

	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		PAClassifier other = (PAClassifier) obj;
		if (weights == null) {
			if (other.weights != null)
				return false;
		} else if (!weights.equals(other.weights))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PA [weights=" + weights + "]";
	}

	public static enum Type {
		STANDARD, PA1, PA2;
	}

}
