package storm.trident.ml.nlp;

import java.util.List;

public interface TextFeaturesExtractor {

	double[] extractFeatures(List<String> documentWords);
}
