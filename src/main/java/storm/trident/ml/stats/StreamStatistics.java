package storm.trident.ml.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StreamStatistics implements Serializable {

	private static final long serialVersionUID = -3873210308112567893L;

	private Type type = Type.FIXED;

	private List<StreamFeatureStatistics> featuresStatistics = new ArrayList<StreamFeatureStatistics>();

	public StreamStatistics() {
	}

	public StreamStatistics(Type type) {
		this.type = type;
	}

	public void update(double[] features) {
		StreamFeatureStatistics featureStatistics;
		for (int i = 0; i < features.length; i++) {
			featureStatistics = this.getStreamStatistics(i);
			featureStatistics.update(features[i]);
		}
	}

	private StreamFeatureStatistics getStreamStatistics(int index) {
		if (this.featuresStatistics.size() < index + 1) {
			StreamFeatureStatistics featureStatistics = this.createFeatureStatistics();
			this.featuresStatistics.add(featureStatistics);
		}
		return this.featuresStatistics.get(index);
	}

	private StreamFeatureStatistics createFeatureStatistics() {
		StreamFeatureStatistics featureStatistics = null;
		switch (this.type) {
		case FIXED:
			featureStatistics = new FixedStreamFeatureStatistics();
			break;
		case ADAPTIVE:
			featureStatistics = new AdaptiveStreamFeatureStatistics();
			break;
		default:
			break;
		}
		return featureStatistics;
	}

	public List<StreamFeatureStatistics> getFeaturesStatistics() {
		return featuresStatistics;
	}

	@Override
	public String toString() {
		return "StreamStatistics [type=" + type + ", featuresStatistics=" + featuresStatistics + "]";
	}

	public static enum Type {
		FIXED, ADAPTIVE;
	}
}
