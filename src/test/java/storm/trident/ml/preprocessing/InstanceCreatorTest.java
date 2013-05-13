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
package storm.trident.ml.preprocessing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import storm.trident.ml.core.Instance;
import storm.trident.tuple.TridentTuple;

public class InstanceCreatorTest {

	@Test
	public void testCreateInstanceWithLabel() {
		// Given
		InstanceCreator<Boolean> instanceCreator = new InstanceCreator<Boolean>();

		TridentTuple tuple = mock(TridentTuple.class);
		Boolean expectedLabel = true;
		double f1 = 0.53;
		double f2 = 0.65;
		when(tuple.size()).thenReturn(3);
		when(tuple.get(0)).thenReturn(expectedLabel);
		when(tuple.getDouble(1)).thenReturn(f1);
		when(tuple.getDouble(2)).thenReturn(f2);

		// When
		Instance<Boolean> actualInstance = instanceCreator.createInstance(tuple);

		// Then
		Instance<Boolean> expectedInstance = new Instance<Boolean>(expectedLabel, new double[] { f1, f2 });
		assertEquals(expectedInstance, actualInstance);
	}

	@Test
	public void testCreateInstanceWithoutLabel() {
		// Given
		InstanceCreator<Boolean> instanceCreator = new InstanceCreator<Boolean>(false);

		TridentTuple tuple = mock(TridentTuple.class);
		double f1 = 0.53;
		double f2 = 0.65;
		when(tuple.size()).thenReturn(2);
		when(tuple.getDouble(0)).thenReturn(f1);
		when(tuple.getDouble(1)).thenReturn(f2);

		// When
		Instance<Boolean> actualInstance = instanceCreator.createInstance(tuple);

		// Then
		Instance<Boolean> expectedInstance = new Instance<Boolean>(new double[] { f1, f2 });
		assertEquals(expectedInstance, actualInstance);
	}
}
