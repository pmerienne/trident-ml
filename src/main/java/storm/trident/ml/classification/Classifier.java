package storm.trident.ml.classification;

import java.io.Serializable;
import java.util.List;

public interface Classifier<L, F> extends Serializable {

	L classify(List<F> features);

	void update(L label, List<F> features);

	void reset();
}
