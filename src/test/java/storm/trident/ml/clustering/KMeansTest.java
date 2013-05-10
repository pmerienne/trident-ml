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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.Datasets;

public class KMeansTest extends ClustererTest {

	@Test
	public void testAgainstGaussianInstances() {
		int nbCluster = 5;
		KMeans kMeans = new KMeans(nbCluster);
		List<Instance<Integer>> samples = Datasets.generateDataForClusterization(nbCluster, 5000);

		double randIndex = this.eval(kMeans, samples);
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.80);
	}

	@Test
	public void testAgainstRealDataset() {
		KMeans kMeans = new KMeans(7);
		double randIndex = this.eval(kMeans, Datasets.getClusteringSamples());
		assertTrue("RAND index " + randIndex + "  isn't good enough : ", randIndex > 0.70);
	}
}
