package storm.trident.ml.classification;

import java.io.Serializable;

public interface Classifier<L> extends Serializable {

	L classify(double[] features);

	void update(L label, double[] features);

	void reset();
}
