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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import storm.trident.ml.util.MathUtil;

public class TFIDF implements TextFeaturesExtractor {

	private Integer corpusSize;
	private Map<String, Double> termsInverseDocumentFrequencies;

	public TFIDF() {
	}

	public TFIDF(List<List<String>> documents, int featureSize) {
		this.init(documents, featureSize);
	}

	@Override
	public double[] extractFeatures(List<String> documentTerms) {
		double[] features = new double[this.termsInverseDocumentFrequencies.size()];

		int i = 0;
		for (String term : this.termsInverseDocumentFrequencies.keySet()) {
			features[i] = this.tfIdf(term, documentTerms);
			i++;
		}

		return MathUtil.normalize(features);
	}

	public void init(List<List<String>> documents, int featureSize) {
		// Init vocabulary
		Vocabulary vocabulary = new Vocabulary();
		for (List<String> document : documents) {
			vocabulary.addAll(document);
		}

		vocabulary.limitWords(featureSize);

		// Calculates idfs
		this.corpusSize = documents.size();
		this.termsInverseDocumentFrequencies = new HashMap<String, Double>(vocabulary.wordCount());
		for (String term : vocabulary) {
			double idf = this.idf(term, documents);
			this.termsInverseDocumentFrequencies.put(term, idf);
		}
	}

	protected double tf(String term, List<String> documentTerms) {
		double tf = 0.0;

		for (String documentTerm : documentTerms) {
			if (documentTerm.equals(term)) {
				tf++;
			}
		}

		return tf;
	}

	protected double idf(String term, List<List<String>> documents) {
		// number of documents where term appears
		double d = 0.0;
		for (List<String> document : documents) {
			if (document.contains(term)) {
				d++;
			}
		}

		return Math.log(this.corpusSize / (1 + d));
	}

	protected double tfIdf(String term, List<String> documentTerms) {
		double idf = this.termsInverseDocumentFrequencies.containsKey(term) ? this.termsInverseDocumentFrequencies.get(term) : Math.log(this.corpusSize);
		double tf = this.tf(term, documentTerms);
		return tf * idf;
	}

	public Integer getCorpusSize() {
		return corpusSize;
	}

	public void setCorpusSize(Integer corpusSize) {
		this.corpusSize = corpusSize;
	}

	public Map<String, Double> getTermsInverseDocumentFrequencies() {
		return termsInverseDocumentFrequencies;
	}

	public void setTermsInverseDocumentFrequencies(Map<String, Double> termsInverseDocumentFrequencies) {
		this.termsInverseDocumentFrequencies = termsInverseDocumentFrequencies;
	}

}
