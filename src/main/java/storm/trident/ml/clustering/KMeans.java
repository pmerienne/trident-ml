package storm.trident.ml.clustering;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.util.MathUtil;

/**
 * 
 * Inspired from http://www.cs.princeton.edu/courses/archive/fall08/cos436/Duda/C/sk_means.htm
 * 
 * @author pmerienne
 * 
 */
public class KMeans implements Clusterer<Double> {

	private List<Long> counts = null;
	private List<List<Double>> centroids;
	private List<List<Double>> initFeatures = new ArrayList<List<Double>>();

	private Integer nbCluster;

	public KMeans(Integer nbCluster) {
		this.nbCluster = nbCluster;
	}

	@Override
	public Integer classify(List<Double> features) {
		if (!this.initCentroidIfPossible(features)) {
			throw new IllegalStateException("KMeans is not ready");
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
		if (!this.initCentroidIfPossible(features)) {
			return null;
		} else {
			Integer nearestCentroid = this.classify(features);

			// Increment count
			this.counts.set(nearestCentroid, this.counts.get(nearestCentroid) + 1);

			// Move centroid
			List<Double> update = MathUtil.multiply(1.0 / this.counts.get(nearestCentroid), MathUtil.subtract(features, this.centroids.get(nearestCentroid)));
			this.centroids.set(nearestCentroid, MathUtil.add(this.centroids.get(nearestCentroid), update));

			return nearestCentroid;
		}
	}

	@Override
	public List<Double> distribution(List<Double> features) {
		if (!this.initCentroidIfPossible(features)) {
			throw new IllegalStateException("KMeans is not ready");
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
	 * @see Arthur, D. and Vassilvitskii, S. (2007). "k-means++: the advantages of careful seeding". Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete
	 *      algorithms. pp. 1027–1035.
	 * 
	 */
	protected void initCentroids(List<Double> features) {
		this.initFeatures.add(features);

		// Init counts
		this.counts = new ArrayList<Long>(this.nbCluster);
		for (int i = 0; i < this.nbCluster; i++) {
			this.counts.add(0L);
		}

		// TODO k-means++
		if (this.initFeatures.size() >= this.nbCluster) {
			this.centroids = new ArrayList<List<Double>>(this.nbCluster);
			for (int i = 0; i < this.nbCluster; i++) {
				this.centroids.add(this.initFeatures.get(i));
			}
		}
	}

	protected boolean initCentroidIfPossible(List<Double> features) {
		if (this.isReady()) {
			return true;
		}

		this.initCentroids(features);
		return this.isReady();
	}

	protected boolean isReady() {
		boolean countsReady = this.counts != null;
		boolean centroidsReady = this.centroids != null;
		return countsReady && centroidsReady;
	}
}
