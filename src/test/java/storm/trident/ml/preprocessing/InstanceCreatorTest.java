package storm.trident.ml.preprocessing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import storm.trident.ml.Instance;
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
