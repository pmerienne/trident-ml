package storm.trident.ml.stats;

public interface StreamFeatureStatistics {

	long getCount();

	double getMean();

	double getVariance();

	double getStdDev();
}
