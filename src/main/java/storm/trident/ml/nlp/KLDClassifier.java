package storm.trident.ml.nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KLDClassifier {

	private Integer nbClasses;
	private List<Vocabulary> classVocabularies = new ArrayList<Vocabulary>();

	public KLDClassifier() {
	}

	public KLDClassifier(Integer nbClasses) {
		this.nbClasses = nbClasses;

		// Init vocabularies
		for (int i = 0; i < nbClasses; i++) {
			this.classVocabularies.add(new Vocabulary());
		}
	}

	public double[] score(String document) {
		double[] score = new double[this.classVocabularies.size()];

		Vocabulary documentVocabulary = VocabularyBuilder.build(document);

		int i = 0;
		for (Vocabulary classVocabulary : this.classVocabularies) {
			score[i] = this.distance(documentVocabulary, classVocabulary);
			i++;
		}

		return score;
	}

	public void update(Integer classIndex, String document) {
		Vocabulary documentVocabulary = VocabularyBuilder.build(document);
		this.classVocabularies.get(classIndex).add(documentVocabulary);
	}

	protected Double distance(Vocabulary documentVocabulary, Vocabulary classVocabulary) {
		Double distance = 0.0;

		Set<String> vocabulary = this.createGlobalVocabulary();
		Double tpc;
		Double tpd;
		for (String term : vocabulary) {
			tpc = this.termProbabilityInCategory(term, classVocabulary);
			tpd = this.termProbabilityInDocument(term, documentVocabulary);
			distance += (tpc - tpd) * Math.log(tpc / tpd);
		}
		return distance;
	}

	protected Double termProbabilityInCategory(String term, Vocabulary classVocabulary) {
		Double probability = classVocabulary.frequency(term);
		if (probability == 0) {
			probability = this.estimateEpsilon();
		} else {
			probability *= this.gamma(classVocabulary);
		}

		return probability;
	}

	protected Double termProbabilityInDocument(String term, Vocabulary documentVocabulary) {
		Double probability = documentVocabulary.frequency(term);
		if (probability == 0) {
			probability = this.estimateEpsilon();
		} else {
			probability *= this.beta(documentVocabulary);
		}

		return probability;
	}

	protected Double gamma(Vocabulary classVocabulary) {
		Double gamma = 1.0;
		Double epsilon = this.estimateEpsilon();

		for (Vocabulary currentVocabulary : this.classVocabularies) {
			if (!classVocabulary.equals(currentVocabulary)) {
				for (String term : currentVocabulary) {
					if (!classVocabulary.contains(term)) {
						gamma -= epsilon;
					}
				}
			}
		}

		return gamma;
	}

	protected Double beta(Vocabulary documentVocabulary) {
		Double beta = 1.0;
		Double epsilon = this.estimateEpsilon();

		for (Vocabulary currentVocabulary : this.classVocabularies) {
			for (String term : currentVocabulary) {
				if (!documentVocabulary.contains(term)) {
					beta -= epsilon;
				}
			}
		}

		return beta;
	}

	protected Double estimateEpsilon() {
		Integer maxSize = 0;

		Integer candidate;
		for (Vocabulary vocabulary : this.classVocabularies) {
			candidate = vocabulary.totalCount();
			if (candidate > maxSize) {
				maxSize = candidate;
			}
		}

		return 1 / maxSize.doubleValue();
	}

	private Set<String> createGlobalVocabulary() {
		Set<String> vocabulary = new HashSet<String>();
		for (Vocabulary classVocabulary : this.classVocabularies) {
			vocabulary.addAll(classVocabulary.wordSet());
		}
		return vocabulary;
	}

}
