package storm.trident.ml.testing;

import java.util.List;

import storm.trident.ml.clustering.Clusterer;

/**
 * Evaluates clusters using the Rand index : W. M. Rand (1971). "Objective criteria for the evaluation of clustering methods". Journal of the American Statistical
 * Association (American Statistical Association) 66 (336): 846–850. doi:10.2307/2284239. JSTOR 2284239
 * 
 * @author pmerienne
 * 
 */
public class RandEvaluator {

	/**
	 * The index produces a result in the range [0,1], where a value of 1.0 indicates that the labels and the calculated clusters are identical. A high value for this
	 * measure generally indicates a high level of agreement between a clustering and the annotated natural classes.
	 * 
	 * @param clusterer
	 * @param samples
	 * @return
	 */
	public Double evaluate(Clusterer<Double> clusterer, List<Sample<Integer, Double>> samples) {
		double tp = 0;
		double tn = 0;
		double fp = 0;
		double fn = 0;

		// Retrieve all samples pair
		for (int i = 0; i < samples.size(); i++) {
			for (int j = i + 1; j < samples.size(); j++) {
				if (i != j) {
					Sample<Integer, Double> sample1 = samples.get(i);
					Integer actual1 = clusterer.classify(sample1.features);

					Sample<Integer, Double> sample2 = samples.get(j);
					Integer actual2 = clusterer.classify(sample2.features);

					if (sample1.label == sample2.label) {
						if (actual1 == actual2) {
							tp++;
						} else {
							fp++;
						}
					} else {
						if (actual1 == actual2) {
							tn++;
						} else {
							fn++;
						}
					}

				}
			}
		}
		return (tp + fn) / (tp + fp + fn + tn);
		// return (tp + tn) / (tp + fp + fn + tn);
	}
}
