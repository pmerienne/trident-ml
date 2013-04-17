package storm.trident.ml.nlp;

import java.util.List;

public interface TextClassifier {

	void update(int label, List<String> documentWords);

	int classify(List<String> documentWords);
}
