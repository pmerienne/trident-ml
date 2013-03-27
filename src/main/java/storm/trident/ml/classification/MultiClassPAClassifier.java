package storm.trident.ml.classification;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

public class MultiClassPAClassifier implements Classifier<Integer, Double> {

	private static final long serialVersionUID = -5163481593640555140L;

	private List<List<Double>> weightVectors;

	private Type type = Type.STANDARD;
	private Double aggressiveness = 0.001;

	private Integer nbClasses;

	public MultiClassPAClassifier() {
	}

	public MultiClassPAClassifier(Integer nbClasses) {
		this.nbClasses = nbClasses;
	}

	public MultiClassPAClassifier(Integer nbClasses, Type type) {
		this.nbClasses = nbClasses;
		this.type = type;
	}

	public MultiClassPAClassifier(Integer nbClasses, Type type, Double aggressiveness) {
		this.nbClasses = nbClasses;
		this.type = type;
		this.aggressiveness = aggressiveness;
	}

	@Override
	public Integer classify(List<Double> features) {
		if (this.weightVectors == null) {
			this.initWeightVectors(features.size());
		}

		Integer prediction = null;
		Double highestScore = -Double.MAX_VALUE;

		Double currentClassScore;
		List<Double> currentWeightVector;
		for (int i = 0; i < this.weightVectors.size(); i++) {
			currentWeightVector = this.weightVectors.get(i);
			currentClassScore = MathUtil.dotProduct(currentWeightVector, features);
			if (currentClassScore > highestScore) {
				prediction = i;
				highestScore = currentClassScore;
			}
		}

		return prediction;
	}

	@Override
	public void update(Integer expectedLabel, List<Double> features) {
		Integer predictedLabel = this.classify(features);

		// lagrange multiplier
		double loss = 1.0 - (MathUtil.dotProduct(this.weightVectors.get(expectedLabel), features) - MathUtil.dotProduct(this.weightVectors.get(predictedLabel),
				features));
		double tau = 0.0;

		if (Type.STANDARD.equals(this.type)) {
			tau = loss / (2 * Math.pow(MathUtil.norm(features), 2));
		} else if (Type.PA1.equals(this.type)) {
			tau = Math.min(this.aggressiveness / 2, loss / (2 * Math.pow(MathUtil.norm(features), 2)));
		} else if (Type.PA2.equals(this.type)) {
			tau = 0.5 * (loss / (Math.pow(MathUtil.norm(features), 2) + (1 / (2 * this.aggressiveness))));
		}

		List<Double> currentWeightVector;
		for (int i = 0; i < this.weightVectors.size(); i++) {
			currentWeightVector = this.weightVectors.get(i);
			if (i != expectedLabel && i != predictedLabel) {
				// No change
			} else if (i == expectedLabel) {
				this.weightVectors.set(i, MathUtil.add(currentWeightVector, MathUtil.multiply(features, tau)));
			} else if (i == predictedLabel) {
				this.weightVectors.set(i, MathUtil.subtract(currentWeightVector, MathUtil.multiply(features, tau)));
			}
		}
	}

	private void initWeightVectors(int featureSize) {
		this.weightVectors = new ArrayList<List<Double>>(this.nbClasses);
		for (int i = 0; i < this.nbClasses; i++) {
			this.weightVectors.add(new ArrayList<Double>(featureSize));
			for (int j = 0; j < featureSize; j++) {
				this.weightVectors.get(i).add(0.0);
			}
		}
	}

	public List<List<Double>> getWeightVectors() {
		return weightVectors;
	}

	public void setWeightVectors(List<List<Double>> weightVectors) {
		this.weightVectors = weightVectors;
	}

	public Integer getNbClasses() {
		return nbClasses;
	}

	public void setNbClasses(Integer nbClasses) {
		this.nbClasses = nbClasses;
	}

	@Override
	public String toString() {
		return "MultiClassPAClassifier [type=" + type + ", aggressiveness=" + aggressiveness + ", nbClasses=" + nbClasses + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aggressiveness == null) ? 0 : aggressiveness.hashCode());
		result = prime * result + ((nbClasses == null) ? 0 : nbClasses.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((weightVectors == null) ? 0 : weightVectors.hashCode());
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
		MultiClassPAClassifier other = (MultiClassPAClassifier) obj;
		if (aggressiveness == null) {
			if (other.aggressiveness != null)
				return false;
		} else if (!aggressiveness.equals(other.aggressiveness))
			return false;
		if (nbClasses == null) {
			if (other.nbClasses != null)
				return false;
		} else if (!nbClasses.equals(other.nbClasses))
			return false;
		if (type != other.type)
			return false;
		if (weightVectors == null) {
			if (other.weightVectors != null)
				return false;
		} else if (!weightVectors.equals(other.weightVectors))
			return false;
		return true;
	}

	public static enum Type {
		STANDARD, PA1, PA2;
	}
}
