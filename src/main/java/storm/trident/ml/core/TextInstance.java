/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
