package storm.trident.ml.nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KLDClassifier {

	private List<Vocabulary> classVocabularies = new ArrayList<Vocabulary>();
	private int maxWordsPerClass = 500000;

	private List<Double> gammas;
	private List<Double> betas;
	private Double espilon;

	public KLDClassifier() {
	}

	public KLDClassifier(int nbClasses) {
		// Init vocabularies
		for (int i = 0; i < nbClasses; i++) {
			this.classVocabularies.add(new Vocabulary());
		}
	}

	public KLDClassifier(int nbClasses, int maxWordsPerClass) {
		this.maxWordsPerClass = maxWordsPerClass;

		// Init vocabularies
		for (int i = 0; i < nbClasses; i++) {
			this.classVocabularies.add(new Vocabulary());
		}
	}

	public int classify(String document) {
		int classIndex = -1;

		double[] distances = this.distance(document);
		double minDistance = Double.POSITIVE_INFINITY;

		int i = 0;
		for (double distance : distances) {
			if (distance < minDistance) {
				minDistance = distance;
				classIndex = i;
			}
			i++;
		}

		return classIndex;
	}

	public double[] distance(String document) {
		return this.distance(document, true);
	}

	public double[] distance(String document, boolean normalize) {
		double[] distance = new double[this.classVocabularies.size()];

		Vocabulary documentVocabulary = VocabularyBuilder.build(document);

		int i = 0;
		for (Vocabulary classVocabulary : this.classVocabularies) {
			distance[i] = this.distance(documentVocabulary, classVocabulary);
			if (normalize) {
				distance[i] /= this.distance(new Vocabulary(), classVocabulary);
			}
			i++;
		}

		return distance;
	}

	public void update(Integer classIndex, String document) {
		Vocabulary documentVocabulary = VocabularyBuilder.build(document);
		Vocabulary classVocabulary = this.classVocabularies.get(classIndex);
		classVocabulary.add(documentVocabulary);
		classVocabulary.limitWords(this.maxWordsPerClass);
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
		if (probability == 0 || probability.equals(Double.NaN)) {
			probability = this.estimateEpsilon();
		} else {
			probability *= this.gamma(classVocabulary);
		}

		return probability;
	}

	protected Double termProbabilityInDocument(String term, Vocabulary documentVocabulary) {
		Double probability = documentVocabulary.frequency(term);
		if (probability == 0 || probability.equals(Double.NaN)) {
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

		return 1 / (10 * maxSize.doubleValue());
	}

	private Set<String> createGlobalVocabulary() {
		Set<String> vocabulary = new HashSet<String>();
		for (Vocabulary classVocabulary : this.classVocabularies) {
			vocabulary.addAll(classVocabulary.wordSet());
		}
		return vocabulary;
	}

}
