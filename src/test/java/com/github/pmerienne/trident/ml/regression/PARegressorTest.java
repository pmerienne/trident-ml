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
package com.github.pmerienne.trident.ml.regression;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.github.pmerienne.trident.ml.regression.PARegressor;
import com.github.pmerienne.trident.ml.testing.data.Datasets;


public class PARegressorTest extends RegressorTest {

	@Test
	public void testWithRandomData() {
		double error = this.eval(new PARegressor(), Datasets.generateDataForRegression(2000, 10));
		assertTrue("Error " + error + " is to big!", error <= 0.01);
	}

	@Ignore("Regressors are not ready for real data")
	@Test
	public void testWithBirthsData() {
		double error = this.eval(new PARegressor(), Datasets.getBIRTHSSamples());
		assertTrue("Error " + error + " is to big!", error <= 0.01);
	}

}
