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
package storm.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.classification.MultiClassPAClassifier.Type;
import storm.trident.ml.core.Instance;
import storm.trident.ml.testing.data.Datasets;

public class MultiClassPATest extends ClassifierTest {

	@Test
	public void testWithGaussianData() {
		List<Instance<Integer>> dataset = Datasets.generateDataForMultiLabelClassification(5000, 10, 10);
		double actualError = this.eval(new MultiClassPAClassifier(10), dataset);
		double actualError1 = this.eval(new MultiClassPAClassifier(10, Type.PA1), dataset);
		double actualError2 = this.eval(new MultiClassPAClassifier(10, Type.PA2), dataset);

		assertTrue("Error " + actualError + " is to big!", actualError < 0.05);
		assertTrue("Error " + actualError1 + " is to big!", actualError1 < 0.05);
		assertTrue("Error " + actualError2 + " is to big!", actualError2 < 0.05);
	}

	@Test
	public void testWithUSPS() {
		double actualError = this.eval(new MultiClassPAClassifier(10), Datasets.getUSPSSamples());
		double actualError1 = this.eval(new MultiClassPAClassifier(10, Type.PA1), Datasets.getUSPSSamples());
		double actualError2 = this.eval(new MultiClassPAClassifier(10, Type.PA2), Datasets.getUSPSSamples());

		assertTrue("Error " + actualError + " is to big!", actualError < 0.30);
		assertTrue("Error " + actualError1 + " is to big!", actualError1 < 0.30);
		assertTrue("Error " + actualError2 + " is to big!", actualError2 < 0.30);
	}
}
