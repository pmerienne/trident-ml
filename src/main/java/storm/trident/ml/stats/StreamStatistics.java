package storm.trident.ml.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StreamStatistics implements Serializable {

	private static final long serialVersionUID = -3873210308112567893L;

	private List<SimpleStreamFeatureStatistics> featuresStatistics = new ArrayList<SimpleStreamFeatureStatistics>();

	public void update(double[] features) {
		for (int i = 0; i < features.length; i++) {
			this.featuresStatistics.get(i).update(features[i]);
		}
	}

	public List<SimpleStreamFeatureStatistics> getFeaturesStatistics() {
		return featuresStatistics;
	}

	public void setFeaturesStatistics(List<SimpleStreamFeatureStatistics> featuresStatistics) {
		this.featuresStatistics = featuresStatistics;
	}

	@Override
	public String toString() {
		return "StreamStatistics [featuresStatistics=" + featuresStatistics + "]";
	}

}
