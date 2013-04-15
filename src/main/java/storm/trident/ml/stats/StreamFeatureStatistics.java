package storm.trident.ml.stats;

public interface StreamFeatureStatistics {

	void update(double feature);

	Long getCount();

	Double getMean();

	Double getVariance();

	Double getStdDev();
}
