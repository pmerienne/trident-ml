package storm.trident.ml.stats;

import java.io.Serializable;
import java.util.LinkedList;

public class AdaptiveStreamFeatureStatistics implements StreamFeatureStatistics, Serializable {

	private static final long serialVersionUID = 6267199325838362436L;

	private long maxSize;

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
	public long getCount() {
		return count;
	}

	@Override
	public double getMean() {
		return this.sum / this.features.size();
	}

	@Override
	public double getVariance() {
		return Math.pow(this.getStdDev(), 2);
	}

	@Override
	public double getStdDev() {
		return Math.sqrt((this.squaresSum / this.features.size()) - Math.pow(this.sum / this.features.size(), 2));
	}

	@Override
	public String toString() {
		return "AdaptiveStreamFeatureStatistics [maxSize=" + maxSize + ", features=" + features + ", sum=" + sum + ", squaresSum=" + squaresSum + ", count="
				+ count + ", mean=" + getMean() + ", variance=" + getVariance() + ", stdDev=" + getStdDev() + "]";
	}

}
