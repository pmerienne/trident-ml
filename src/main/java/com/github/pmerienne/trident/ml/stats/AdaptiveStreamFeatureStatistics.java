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
package com.github.pmerienne.trident.ml.stats;

import java.io.Serializable;
import java.util.LinkedList;

public class AdaptiveStreamFeatureStatistics implements StreamFeatureStatistics, Serializable {

	private static final long serialVersionUID = 6267199325838362436L;

	private long maxSize = 1000;

	private LinkedList<Double> features = new LinkedList<Double>();

	private long count = 0L;
	private double sum = 0L;
	private double squaresSum = 0.0;

	public AdaptiveStreamFeatureStatistics() {
	}

	public AdaptiveStreamFeatureStatistics(long maxSize) {
		this.maxSize = maxSize;
	}

	public void update(double feature) {
		this.count++;
		this.features.add(feature);
		this.sum += feature;
		this.squaresSum += Math.pow(feature, 2);

		if (this.features.size() > maxSize) {
			double first = this.features.removeFirst();
			this.sum -= first;
			this.squaresSum -= Math.pow(first, 2);
		}
	}

	@Override
	public Long getCount() {
		return count;
	}

	@Override
	public Double getMean() {
		return this.sum / this.features.size();
	}

	@Override
	public Double getVariance() {
		return Math.pow(this.getStdDev(), 2);
	}

	@Override
	public Double getStdDev() {
		return Math.sqrt((this.squaresSum / this.features.size()) - Math.pow(this.sum / this.features.size(), 2));
	}

	@Override
	public String toString() {
		return "AdaptiveStreamFeatureStatistics [maxSize=" + maxSize + ", features=" + features + ", sum=" + sum + ", squaresSum=" + squaresSum + ", count="
				+ count + ", mean=" + getMean() + ", variance=" + getVariance() + ", stdDev=" + getStdDev() + "]";
	}

}
