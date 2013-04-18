package storm.trident.ml.core;

import java.io.Serializable;
import java.util.List;

public class TextInstance<L> implements Serializable {

	private static final long serialVersionUID = 8825994169660110735L;

	public final L label;
	public final List<String> tokens;

	public TextInstance(List<String> tokens) {
		this.label = null;
		this.tokens = tokens;
	}

	public TextInstance(L label, List<String> tokens) {
		this.label = label;
		this.tokens = tokens;
	}

	public L getLabel() {
		return label;
	}

	public List<String> getTokens() {
		return tokens;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
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
		TextInstance other = (TextInstance) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TextInstance [label=" + label + ", tokens=" + tokens + "]";
	}

}
