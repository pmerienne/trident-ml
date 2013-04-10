package storm.trident.ml.regression;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * Passive-Aggresive regressor.
 * 
 * @see Online Passive-Aggressive Algorithms
 * 
 *      Koby Crammer, Ofer Dekel, Joseph Keshet, Shai Shalev-Shwartz, Yoram
 *      Singer; 7(Mar):551--585, 2006.
 * @author pmerienne
 * 
 */
public class PARegressor implements Regressor {

	private static final long serialVersionUID = -5163481593640555140L;

	private List<Double> weights;

	private Double epsilon = 0.01;

	private Type type = Type.STANDARD;
	private Double aggressiveness = 0.001;

	public PARegressor() {
	}

	public PARegressor(Double epsilon) {
		this.epsilon = epsilon;
	}

	public PARegressor(Type type) {
		this.type = type;
	}

	public PARegressor(Type type, Double aggressiveness) {
		this.type = type;
		this.aggressiveness = aggressiveness;
	}

	public PARegressor(Double epsilon, Type type) {
		this.epsilon = epsilon;
		this.type = type;
	}

	public PARegressor(Double epsilon, Type type, Double aggressiveness) {
		this.epsilon = epsilon;
		this.type = type;
		this.aggressiveness = aggressiveness;
	}

	@Override
	public Double predict(List<Double> features) {
		if (this.weights == null) {
			this.init(features.size());
		}

		Double prediction = MathUtil.dotProduct(features, this.weights);
		return prediction;
	}

	@Override
	public void update(Double expected, List<Double> features) {
		if (this.weights == null) {
			this.init(features.size());
		}

		Double prediction = this.predict(features);

		double sign = expected - prediction > 0 ? 1.0 : -1.0;
		double loss = Math.max(0.0, Math.abs(prediction - expected) - this.epsilon);
		double update = 0;

		if (Type.STANDARD.equals(this.type)) {
			update = loss / Math.pow(MathUtil.norm(features), 2);
		} else if (Type.PA1.equals(this.type)) {
			update = Math.min(this.aggressiveness, loss / Math.pow(MathUtil.norm(features), 2));
		} else if (Type.PA2.equals(this.type)) {
			update = loss / (Math.pow(MathUtil.norm(features), 2) + (1.0 / (2 * this.aggressiveness)));
		}

		List<Double> scaledFeatures = MathUtil.multiply(features, update * sign);
		this.weights = MathUtil.add(this.weights, scaledFeatures);
	}

	protected void init(int featureSize) {
		// Init weights
		this.weights = new ArrayList<Double>(featureSize);
		for (int i = 0; i < featureSize; i++) {
			this.weights.add(0.0);
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
		PARegressor other = (PARegressor) obj;
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
