package storm.trident.ml.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/***
 * Text classifier and feature extractor using Kullback-Leibler Distance.
 * 
 * See "Using Kullback-Leibler Distance for Text Categorization Brigitte Bigi"
 * 
 * @author pmerienne
 * 
 */
public class KLDClassifier implements TextClassifier<Integer>, TextFeaturesExtractor, Serializable {

	private static final long serialVersionUID = 3869875629653284342L;

	private int maxWordsPerClass = 500000;
	private double thresholdFactor = 10.0;
	private boolean normalize = true;

	private List<Vocabulary> classVocabularies = new ArrayList<Vocabulary>();
	private List<Double> gammas = new ArrayList<Double>();
	private Double espilon = null;

	public KLDClassifier() {
	}

	public KLDClassifier(int nbClasses) {
		this(nbClasses, 500000, 10.0, true);
	}

	public KLDClassifier(int nbClasses, int maxWordsPerClass) {
		this(nbClasses, maxWordsPerClass, 10.0, true);
	}

	public KLDClassifier(int nbClasses, int maxWordsPerClass, double thresholdFactor, boolean normalize) {
		this.maxWordsPerClass = maxWordsPerClass;
		this.thresholdFactor = thresholdFactor;
		this.normalize = normalize;

		for (int i = 0; i < nbClasses; i++) {
			this.classVocabularies.add(new Vocabulary());
			this.gammas.add(null);
		}
	}

	@Override
	public double[] extractFeatures(List<String> documentWords) {
		Vocabulary documentVocabulary = new Vocabulary(documentWords);

		Set<String> vocabulary = this.createGlobalVocabulary();
		int vocabularySize = vocabulary.size();
		int nbClasses = this.classVocabularies.size();

		double[] features = new double[vocabularySize * nbClasses];

		double beta = this.caculateBeta(documentVocabulary);
		Double tpd, tpc;
		int i = 0;
		for (String word : vocabulary) {
			tpd = this.wordProbabilityInDocument(word, documentVocabulary, beta);
			for (int j = 0; j < nbClasses; j++) {
				tpc = this.wordProbabilityInCategory(word, 0);
				features[j * vocabularySize + i] = (tpc - tpd) * Math.log(tpc / tpd);
			}
			i++;
		}

		return features;
	}

	@Override
	public void update(Integer classIndex, List<String> documentWords) {
		// Update class vocabulary
		Vocabulary classVocabulary = this.classVocabularies.get(classIndex);
		classVocabulary.addAll(documentWords);
		classVocabulary.limitWords(this.maxWordsPerClass);

		// Reset associated gamma
		this.gammas.set(classIndex, null);

		// Reset epsilon
		this.espilon = null;
	}

	@Override
	public Integer classify(List<String> documentWords) {
		int classIndex = -1;

		double[] distances = this.distance(documentWords);

		// Find minimum distance
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

	public double[] distance(List<String> documentWords) {
		double[] distance = new double[this.classVocabularies.size()];

		Vocabulary documentVocabulary = new Vocabulary(documentWords);

		double beta = this.caculateBeta(documentVocabulary);
		double betaZero = this.caculateBeta(new Vocabulary());

		for (int classIndex = 0; classIndex < this.classVocabularies.size(); classIndex++) {
			distance[classIndex] = this.distance(documentVocabulary, classIndex, beta);

			if (this.normalize) {
				distance[classIndex] /= this.distance(new Vocabulary(), classIndex, betaZero);
			}
		}

		return distance;
	}

	protected Double distance(Vocabulary documentVocabulary, int classIndex, double beta) {
		Double distance = 0.0;

		Set<String> vocabulary = this.createGlobalVocabulary();
		Double tpc;
		Double tpd;
		for (String word : vocabulary) {
			tpc = this.wordProbabilityInCategory(word, classIndex);
			tpd = this.wordProbabilityInDocument(word, documentVocabulary, beta);
			distance += (tpc - tpd) * Math.log(tpc / tpd);
		}
		return distance;
	}

	protected Double wordProbabilityInCategory(String word, int classIndex) {
		Vocabulary classVocabulary = this.classVocabularies.get(classIndex);

		Double probability = classVocabulary.frequency(word);
		if (probability == 0 || probability.equals(Double.NaN)) {
			probability = this.estimateEpsilon();
		} else {
			probability *= this.getGamma(classIndex);
		}

		return probability;
	}

	protected Double wordProbabilityInDocument(String word, Vocabulary documentVocabulary, double beta) {
		Double probability = documentVocabulary.frequency(word);
		if (probability == 0 || probability.equals(Double.NaN)) {
			probability = this.getEpsilon();
		} else {
			probability *= beta;
		}

		return probability;
	}

	protected double getEpsilon() {
		if (this.espilon == null) {
			this.espilon = this.estimateEpsilon();
		}
		return this.espilon;
	}

	protected double estimateEpsilon() {
		Integer maxSize = 0;

		Integer candidate;
		for (Vocabulary vocabulary : this.classVocabularies) {
			candidate = vocabulary.totalCount();
			if (candidate > maxSize) {
				maxSize = candidate;
			}
		}

		return 1 / (this.thresholdFactor * maxSize.doubleValue());
	}

	protected double getGamma(int classIndex) {
		Double gamma = this.gammas.get(classIndex);
		if (gamma == null) {
			gamma = this.calculateGamma(classIndex);
			this.gammas.set(classIndex, gamma);
		}

		return gamma;
	}

	protected double calculateGamma(int classIndex) {
		Double gamma = 1.0;
		Double epsilon = this.getEpsilon();

		Vocabulary classVocabulary = this.classVocabularies.get(classIndex);
		Set<String> globalVocabulary = this.createGlobalVocabulary();

		for (String word : globalVocabulary) {
			if (!classVocabulary.contains(word)) {
				gamma -= epsilon;
			}
		}

		return gamma;
	}

	protected double caculateBeta(Vocabulary documentVocabulary) {
		Double beta = 1.0;
		Double epsilon = this.getEpsilon();

		Set<String> globalVocabulary = this.createGlobalVocabulary();

		for (String word : globalVocabulary) {
			if (!documentVocabulary.contains(word)) {
				beta -= epsilon;
			}
		}

		return beta;
	}

	private Set<String> createGlobalVocabulary() {
		Set<String> vocabulary = new HashSet<String>();
		for (Vocabulary classVocabulary : this.classVocabularies) {
			vocabulary.addAll(classVocabulary.wordSet());
		}
		return vocabulary;
	}

}
