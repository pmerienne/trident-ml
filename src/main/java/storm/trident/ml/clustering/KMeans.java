package storm.trident.ml.clustering;

import java.io.Serializable;
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
public class KMeans implements Clusterer, Serializable {

	private static final long serialVersionUID = 338231277453149972L;

	private List<Long> counts = null;
	private double[][] centroids;
	private List<double[]> initFeatures = new ArrayList<double[]>();

	private Integer nbCluster;

	public KMeans(Integer nbCluster) {
		this.nbCluster = nbCluster;
	}

	@Override
	public Integer classify(double[] features) {
		if (!this.initCentroidIfPossible(features)) {
			throw new IllegalStateException("KMeans is not ready");
		}

		// Find nearest centroid
		Integer nearestCentroidIndex = 0;
		Double minDistance = Double.MAX_VALUE;
		double[] currentCentroid;
		Double currentDistance;
		for (int i = 0; i < this.nbCluster; i++) {
			currentCentroid = this.centroids[i];
			currentDistance = MathUtil.euclideanDistance(currentCentroid, features);
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
				nearestCentroidIndex = i;
			}
		}

		return nearestCentroidIndex;
	}

	@Override
	public Integer update(double[] features) {
		if (!this.initCentroidIfPossible(features)) {
			return null;
		} else {
			Integer nearestCentroid = this.classify(features);

			// Increment count
			this.counts.set(nearestCentroid, this.counts.get(nearestCentroid) + 1);

			// Move centroid
			double[] update = MathUtil.mult(MathUtil.subtract(features, this.centroids[nearestCentroid]), 1.0 / this.counts.get(nearestCentroid));
			this.centroids[nearestCentroid] = MathUtil.add(this.centroids[nearestCentroid], update);

			return nearestCentroid;
		}
	}

	@Override
	public double[] distribution(double[] features) {
		if (!this.initCentroidIfPossible(features)) {
			throw new IllegalStateException("KMeans is not ready");
		}

		double[] distribution = new double[this.nbCluster];
		double[] currentCentroid;
		for (int i = 0; i < this.nbCluster; i++) {
			currentCentroid = this.centroids[i];
			distribution[i] = MathUtil.euclideanDistance(currentCentroid, features);
		}

		return distribution;
	}

	@Override
	public double[][] getCentroids() {
		return this.centroids;
	}

	/**
	 * Currently : random initialization
	 * 
	 * TODO : Init clusters using the k-means++ algorithm. (Arthur, D. and
	 * Vassilvitskii, S. (2007). "k-means++: the advantages of careful seeding".
	 * 
	 * 
	 */
	protected void initCentroids(double[] features) {
		this.initFeatures.add(features);

		// Init counts
		this.counts = new ArrayList<Long>(this.nbCluster);
		for (int i = 0; i < this.nbCluster; i++) {
			this.counts.add(0L);
		}

		// TODO k-means++
		if (this.initFeatures.size() >= 10 * this.nbCluster) {
			this.centroids = new double[this.nbCluster][features.length];
			for (int i = 0; i < this.nbCluster; i++) {
				this.centroids[i] = this.initFeatures.get(i);
			}
			this.initFeatures.clear();
		}
	}

	protected boolean initCentroidIfPossible(double[] features) {
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

	@Override
	public void reset() {
		this.counts = null;
		this.centroids = null;
		this.initFeatures = new ArrayList<double[]>();
	}
}
