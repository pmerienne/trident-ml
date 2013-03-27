package storm.trident.ml.clustering;

import java.util.List;

public interface Clusterer<F> {

	/**
	 * Classifies a given sample and updates clusters.
	 * 
	 * @param features
	 * @return
	 */
	Integer classify(List<F> features);


	/**
	 * Updates clusters with a given sample and return classification.
	 * 
	 * @param features
	 * @return
	 */
	Integer update(List<F> features);
	
	/**
	 * Predicts the cluster memberships for a given instance.
	 * 
	 * @param features
	 * @return
	 */
	List<Double> distribution(List<F> features);

	/**
	 * Returns learned clusters as a {@link List} of feature's means
	 * 
	 * @return
	 */
	List<List<F>> getCentroids();
}
