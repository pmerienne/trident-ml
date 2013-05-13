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
package storm.trident.ml.clustering;

import java.util.List;

import storm.trident.ml.clustering.Clusterer;
import storm.trident.ml.core.Instance;

/**
 * Evaluates clusters using the Rand index : W. M. Rand (1971).
 * "Objective criteria for the evaluation of clustering methods". Journal of the
 * American Statistical Association (American Statistical Association) 66 (336):
 * 846–850. doi:10.2307/2284239. JSTOR 2284239
 * 
 * @author pmerienne
 * 
 */
public class RandEvaluator {

	/**
	 * The index produces a result in the range [0,1], where a value of 1.0
	 * indicates that the labels and the calculated clusters are identical. A
	 * high value for this measure generally indicates a high level of agreement
	 * between a clustering and the annotated natural classes.
	 * 
	 * @param clusterer
	 * @param samples
	 * @return
	 */
	public Double evaluate(Clusterer clusterer, List<Instance<Integer>> samples) {
		double tp = 0;
		double tn = 0;
		double fp = 0;
		double fn = 0;

		// Retrieve all samples pair
		for (int i = 0; i < samples.size(); i++) {
			for (int j = i + 1; j < samples.size(); j++) {
				if (i != j) {
					Instance<Integer> sample1 = samples.get(i);
					Integer actual1 = clusterer.classify(sample1.features);

					Instance<Integer> sample2 = samples.get(j);
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
