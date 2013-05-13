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
package com.github.pmerienne.trident.ml.classification;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.github.pmerienne.trident.ml.classification.PerceptronClassifier;
import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.testing.data.Datasets;


public class PerceptronTest extends ClassifierTest {

	@Test
	public void testWithNand() {
		List<Instance<Boolean>> samples = Datasets.generatedNandInstances(100);
		double error = this.eval(new PerceptronClassifier(), samples);
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}

	@Test
	public void testWithGaussianData() {
		double error = this.eval(new PerceptronClassifier(), Datasets.generateDataForClassification(100, 10));
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}

	@Test
	public void testWithSPAMData() {
		double error = this.eval(new PerceptronClassifier(), Datasets.getSpamSamples());
		assertTrue("Error " + error + " is to big!", error < 0.05);
	}

}
