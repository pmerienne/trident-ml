package storm.trident.ml;

import java.io.Serializable;
import java.util.Arrays;

import storm.trident.ml.testing.data.Sample;

public class Instance<L> implements Serializable {

	private static final long serialVersionUID = -5378422729499109652L;

	public final L label;
	public final double[] features;

	public Instance(L label, double[] features) {
		this.label = label;
		this.features = features;
	}

	public Instance(double[] features) {
		this.label = null;
		this.features = features;
	}

	public L getLabel() {
		return label;
	}

	public double[] getFeatures() {
		return features;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sample other = (Sample) obj;
		if (features == null) {
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instance [label=" + label + ", features=" + Arrays.toString(features) + "]";
	}

}
