package storm.trident.ml.regression;


public interface Regressor {

	Double predict(double[] features);

	void update(Double expected, double[] features);

	void reset();
}
