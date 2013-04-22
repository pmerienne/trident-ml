package storm.trident.ml.clustering;

import java.util.List;

public interface Clusterer {

	/**
	 * Classifies a given sample and updates clusters.
	 * 
	 * @param features
	 * @return
	 */
	Integer classify(double[] features);

	/**
	 * Updates clusters with a given sample and return classification.
	 * 
	 * @param features
	 * @return
	 */
	Integer update(double[] features);

	/**
	 * Predicts the cluster memberships for a given instance.
	 * 
	 * @param features
	 * @return
	 */
	double[] distribution(double[] features);

	/**
	 * Returns learned clusters as a {@link List} of feature's means
	 * 
	 * @return
	 */
	double[][] getCentroids();
	
	void reset();
}
