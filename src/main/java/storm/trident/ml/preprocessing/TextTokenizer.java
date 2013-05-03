package storm.trident.ml.preprocessing;

import java.util.List;

public interface TextTokenizer {

	List<String> tokenize(String text);
}
