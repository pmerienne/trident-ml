package storm.trident.ml.regression;

import java.util.List;

public interface Regressor {

	Double predict(List<Double> features);

	void update(Double expected, List<Double> features);
}
