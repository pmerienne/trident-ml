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
package storm.trident.ml.nlp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

public class Vocabulary implements Iterable<String>, Serializable {

	private static final long serialVersionUID = 7827671824674205961L;

	private TreeMap<String, Integer> wordCounts = new ValueComparableMap<String, Integer>(Ordering.natural().reverse());

	private Integer size = 0;

	public Vocabulary() {
	}

	public Vocabulary(List<String> words) {
		this.addAll(words);
	}

	public void add(String word) {
		Integer actualCount = this.wordCounts.get(word);
		if (actualCount == null) {
			actualCount = 1;
		} else {
			actualCount++;
		}
		this.wordCounts.put(word, actualCount);
		this.size++;
	}

	public void addAll(List<String> words) {
		for (String word : words) {
			this.add(word);
		}
	}

	public void limitWords(Integer maxWords) {
		String lessFrequentWord;
		Integer lowestCount;
		while (this.wordCount() > maxWords) {
			lessFrequentWord = this.wordCounts.lastKey();
			lowestCount = this.wordCounts.remove(lessFrequentWord);
			this.size -= lowestCount;
		}
	}

	public Integer count(String word) {
		Integer actualCount = this.wordCounts.get(word);
		if (actualCount == null) {
			actualCount = 0;
		}
		return actualCount;
	}

	public Double frequency(String word) {
		return this.count(word).doubleValue() / this.size.doubleValue();
	}

	public Boolean contains(String word) {
		return this.wordCounts.containsKey(word);
	}

	public Integer wordCount() {
		return this.wordCounts.size();
	}

	public Integer totalCount() {
		return this.size;
	}

	@Override
	public Iterator<String> iterator() {
		return this.wordCounts.keySet().iterator();
	}

	public Set<String> wordSet() {
		return this.wordCounts.keySet();
	}

	@Override
	public String toString() {
		return "Vocabulary [size=" + size + ", wordCounts=" + wordCounts + "]";
	}

	/**
	 * <pre>
	 * See <a href=http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java/1283722#comment14899161_1283722>how-to-sort-a-mapkey-value-on-the-values-in-java</a>
	 * </pre>
	 * 
	 * @param <K>
	 * @param <V>
	 */
	private static class ValueComparableMap<K extends Comparable<K>, V> extends TreeMap<K, V> {

		private static final long serialVersionUID = 1476556231893371136L;
		// A map for doing lookups on the keys for comparison so we don't get
		// infinite loops
		private final Map<K, V> valueMap;

		ValueComparableMap(final Ordering<? super V> partialValueOrdering) {
			this(partialValueOrdering, new HashMap<K, V>());
		}

		private ValueComparableMap(Ordering<? super V> partialValueOrdering, HashMap<K, V> valueMap) {
			super(partialValueOrdering // Apply the value ordering
					.onResultOf(Functions.forMap(valueMap)) // On the result of
															// getting the value
															// for the key from
															// the map
					.compound(Ordering.natural())); // as well as ensuring that
													// the keys don't get
													// clobbered
			this.valueMap = valueMap;
		}

		@Override
		public V get(Object key) {
			return this.valueMap.get(key);
		}

		@Override
		public boolean containsKey(Object key) {
			return this.valueMap.containsKey(key);
		}

		public V put(K k, V v) {
			if (valueMap.containsKey(k)) {
				// remove the key in the sorted set before adding the key again
				super.remove(k);
			}
			valueMap.put(k, v); // To get "real" unsorted values for the
								// comparator
			return super.put(k, v); // Put it in value order
		}

		@Override
		public V remove(Object key) {
			super.remove(key);
			return this.valueMap.remove(key);
		}
	}

}
