/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pmerienne.trident.ml.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.pmerienne.trident.ml.util.MathUtil;


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
		if (!this.isReady()) {
			throw new IllegalStateException("KMeans is not ready yet");
		}

		// Find nearest centroid
		Integer nearestCentroidIndex = this.nearestCentroid(features);
		return nearestCentroidIndex;
	}

	@Override
	public Integer update(double[] features) {
		if (!this.isReady()) {
			this.initIfPossible(features);
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
		if (!this.isReady()) {
			throw new IllegalStateException("KMeans is not ready yet");
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

	protected Integer nearestCentroid(double[] features) {
		// Find nearest centroid
		Integer nearestCentroidIndex = 0;

		Double minDistance = Double.MAX_VALUE;
		double[] currentCentroid;
		Double currentDistance;
		for (int i = 0; i < this.centroids.length; i++) {
			currentCentroid = this.centroids[i];
			if (currentCentroid != null) {
				currentDistance = MathUtil.euclideanDistance(currentCentroid, features);
				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					nearestCentroidIndex = i;
				}
			}
		}

		return nearestCentroidIndex;
	}

	protected boolean isReady() {
		boolean countsReady = this.counts != null;
		boolean centroidsReady = this.centroids != null;
		return countsReady && centroidsReady;
	}

	protected void initIfPossible(double[] features) {
		this.initFeatures.add(features);

		// magic number : 10 ??!
		if (this.initFeatures.size() >= 10 * this.nbCluster) {
			this.initCentroids();
		}
	}

	/**
	 * Init clusters using the k-means++ algorithm. (Arthur, D. and
	 * Vassilvitskii, S. (2007). "k-means++: the advantages of careful seeding".
	 * 
	 */
	protected void initCentroids() {
		// Init counts
		this.counts = new ArrayList<Long>(this.nbCluster);
		for (int i = 0; i < this.nbCluster; i++) {
			this.counts.add(0L);
		}

		this.centroids = new double[this.nbCluster][];

		Random random = new Random();

		// Choose one centroid uniformly at random from among the data points.
		final double[] firstCentroid = this.initFeatures.remove(random.nextInt(this.initFeatures.size()));
		this.centroids[0] = firstCentroid;

		double[] dxs;

		for (int j = 1; j < this.nbCluster; j++) {
			// For each data point x, compute D(x)
			dxs = this.computeDxs();

			// Add one new data point as a center.
			double[] features;
			double r = random.nextDouble() * dxs[dxs.length - 1];
			for (int i = 0; i < dxs.length; i++) {
				if (dxs[i] >= r) {
					features = this.initFeatures.remove(i);
					this.centroids[j] = features;
					break;
				}
			}
		}

		this.initFeatures.clear();
	}

	/**
	 * For each features in {@link KMeans#initFeatures}, compute D(x), the
	 * distance between x and the nearest center that has already been chosen.
	 * 
	 * @return
	 */
	protected double[] computeDxs() {
		double[] dxs = new double[this.initFeatures.size()];

		int sum = 0;
		double[] features;
		int nearestCentroidIndex;
		double[] nearestCentroid;
		for (int i = 0; i < this.initFeatures.size(); i++) {
			features = this.initFeatures.get(i);
			nearestCentroidIndex = this.nearestCentroid(features);
			nearestCentroid = this.centroids[nearestCentroidIndex];
			sum += Math.pow(MathUtil.euclideanDistance(features, nearestCentroid), 2);
			dxs[i] = sum;
		}

		return dxs;
	}

	@Override
	public void reset() {
		this.counts = null;
		this.centroids = null;
		this.initFeatures = new ArrayList<double[]>();
	}
}
