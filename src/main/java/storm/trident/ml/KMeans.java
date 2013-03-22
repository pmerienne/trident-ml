package storm.trident.ml;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * 
 * Inspired from
 * http://www.cs.princeton.edu/courses/archive/fall08/cos436/Duda/C/sk_means.htm
 * 
 * @author pmerienne
 * 
 */
public class KMeans implements Clusterer<Double> {

	private List<Long> counts = null;
	private List<List<Double>> centroids;

	private Integer nbCluster;

	public KMeans(Integer nbCluster) {
		this.nbCluster = nbCluster;
	}

	@Override
	public Integer classify(List<Double> features) {
		if (this.centroids == null) {
			this.initCentroids(features);
		}

		// Find nearest centroid
		Integer nearestCentroidIndex = 0;
		Double minDistance = Double.MAX_VALUE;
		List<Double> currentCentroid;
		Double currentDistance;
		for (int i = 0; i < this.nbCluster; i++) {
			currentCentroid = this.centroids.get(i);
			currentDistance = MathUtil.euclideanDistance(currentCentroid, features);
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
				nearestCentroidIndex = i;
			}
		}

		return nearestCentroidIndex;
	}

	@Override
	public Integer update(List<Double> features) {
		Integer nearestCentroid = this.classify(features);

		// Increment count
		this.counts.set(nearestCentroid, this.counts.get(nearestCentroid) + 1);

		// Move centroid
		List<Double> update = MathUtil.multiply(1.0 / this.counts.get(nearestCentroid), MathUtil.subtract(features, this.centroids.get(nearestCentroid)));
		this.centroids.set(nearestCentroid, MathUtil.add(this.centroids.get(nearestCentroid), update));

		return nearestCentroid;
	}

	@Override
	public List<Double> distribution(List<Double> features) {
		if (this.centroids == null) {
			this.initCentroids(features);
		}

		List<Double> distribution = new ArrayList<Double>();
		List<Double> currentCentroid;
		for (int i = 0; i < this.nbCluster; i++) {
			currentCentroid = this.centroids.get(i);
			distribution.add(MathUtil.euclideanDistance(currentCentroid, features));
		}

		return distribution;
	}

	@Override
	public List<List<Double>> getCentroids() {
		return this.centroids;
	}

	/**
	 * Init clusters using the k-means++ algorithm.
	 * 
	 * @see Arthur, D. and Vassilvitskii, S. (2007).
	 *      "k-means++: the advantages of careful seeding". Proceedings of the
	 *      eighteenth annual ACM-SIAM symposium on Discrete algorithms. pp.
	 *      1027–1035.
	 * 
	 */
	protected void initCentroids(List<Double> firstFeatures) {
		// Init counts
		this.counts = new ArrayList<Long>(this.nbCluster);
		for (int i = 0; i < this.nbCluster; i++) {
			this.counts.add(0L);
		}

		// TODO k-means++
		this.centroids = new ArrayList<List<Double>>(this.nbCluster);
		for (int i = 0; i < this.nbCluster; i++) {
			this.centroids.add(new ArrayList<Double>(firstFeatures.size()));
			for (int j = 0; j < firstFeatures.size(); j++) {
				this.centroids.get(i).add(0.0);
			}
		}
	}
}
