package storm.trident.ml.stats;

public interface StreamFeatureStatistics {

	void update(double feature);

	long getCount();

	double getMean();

	double getVariance();

	double getStdDev();
}
