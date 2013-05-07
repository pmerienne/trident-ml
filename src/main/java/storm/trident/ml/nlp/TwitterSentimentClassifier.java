package storm.trident.ml.nlp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import storm.trident.ml.classification.Classifier;
import storm.trident.ml.classification.PAClassifier;
import storm.trident.ml.core.TextInstance;
import storm.trident.ml.testing.data.Datasets;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class TwitterSentimentClassifier extends BaseFunction implements Serializable {

	private static final long serialVersionUID = 1553274753609262633L;

	protected TextFeaturesExtractor featuresExtractor;
	protected Classifier<Boolean> classifier;

	public TwitterSentimentClassifier() {
		try {
			this.featuresExtractor = Builder.loadFeatureExtractor();
			this.classifier = Builder.loadClassifier();
		} catch (IOException e) {
			throw new RuntimeException("Unable to load TwitterSentimentClassifier : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		TextInstance<Boolean> instance = (TextInstance<Boolean>) tuple.get(0);
		boolean prediction = this.classify(instance.tokens);
		collector.emit(new Values(prediction));
	}

	protected Boolean classify(List<String> documentWords) {
		double[] features = this.featuresExtractor.extractFeatures(documentWords);
		Boolean prediction = this.classifier.classify(features);
		return prediction;
	}

	protected static class Builder {

		private final static File TEXT_FEATURES_EXTRACTOR_FILE = new File(Builder.class.getResource("/twitter-sentiment-classifier-extractor.json").getFile());
		private final static File CLASSIFIER_FILE = new File(Builder.class.getResource("/twitter-sentiment-classifier-classifier.json").getFile());

		private final static ObjectMapper MAPPER = new ObjectMapper();

		public static void main(String[] args) throws IOException {
			// Get some tweets
			List<TextInstance<Boolean>> dataset = Datasets.getSmallTwitterSamples();
			List<List<String>> documents = new ArrayList<List<String>>();
			for (TextInstance<Boolean> instance : dataset) {
				documents.add(instance.tokens);
			}

			// Init feature extractor
			TFIDF featuresExtractor = new TFIDF(documents, 10000);

			// Init and train classifier
			PAClassifier classifier = new PAClassifier();
			double[] features;
			for (TextInstance<Boolean> instance : dataset) {
				features = featuresExtractor.extractFeatures(instance.tokens);
				classifier.update(instance.label, features);
			}

			// save them
			save(featuresExtractor, classifier);
		}

		protected static void save(TFIDF featuresExtractor, PAClassifier classifier) throws IOException {
			MAPPER.writeValue(TEXT_FEATURES_EXTRACTOR_FILE, featuresExtractor);
			MAPPER.writeValue(CLASSIFIER_FILE, classifier);
		}

		public static TextFeaturesExtractor loadFeatureExtractor() throws IOException {
			return MAPPER.readValue(TEXT_FEATURES_EXTRACTOR_FILE, TFIDF.class);
		}

		public static Classifier<Boolean> loadClassifier() throws IOException {
			return MAPPER.readValue(CLASSIFIER_FILE, PAClassifier.class);
		}
	}
}
