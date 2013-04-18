package storm.trident.ml.preprocessing;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import storm.trident.ml.core.TextInstance;
import storm.trident.tuple.TridentTuple;

public class TextInstanceCreatorTest {

	@Test
	public void testExtractTokens() {
		// Given
		TextInstanceCreator<Boolean> instanceCreator = new TextInstanceCreator<Boolean>(false);

		// When
		List<String> actualWords = instanceCreator.extractTokens("I can't argue with some arguments on argus with argues");

		// Then
		List<String> expectedWords = Arrays.asList("i", "can't", "argu", "some", "argument", "argu", "argu");
		assertEquals(expectedWords, actualWords);
	}

	@Test
	public void testCreateInstanceWithLabel() {
		// Given
		TextInstanceCreator<Integer> instanceCreator = new TextInstanceCreator<Integer>(true);

		Integer expectedLabel = 1;
		String expectedText = "I can't argue with some arguments on argus with argues";
		TridentTuple tuple = mock(TridentTuple.class);
		when(tuple.get(0)).thenReturn(expectedLabel);
		when(tuple.getString(1)).thenReturn(expectedText);

		// When
		TextInstance<Integer> actualInstance = instanceCreator.createInstance(tuple);

		// Then
		TextInstance<Integer> expectedInstance = new TextInstance<Integer>(expectedLabel, Arrays.asList("i", "can't", "argu", "some", "argument", "argu", "argu"));
		assertEquals(expectedInstance, actualInstance);
	}

	@Test
	public void testCreateInstanceWithoutLabel() {
		// Given
		TextInstanceCreator<Integer> instanceCreator = new TextInstanceCreator<Integer>(false);

		String expectedText = "I can't argue with some arguments on argus with argues";
		TridentTuple tuple = mock(TridentTuple.class);
		when(tuple.getString(1)).thenReturn(expectedText);

		// When
		TextInstance<Integer> actualInstance = instanceCreator.createInstance(tuple);

		// Then
		TextInstance<Integer> expectedInstance = new TextInstance<Integer>(Arrays.asList("i", "can't", "argu", "some", "argument", "argu", "argu"));
		assertEquals(expectedInstance, actualInstance);
	}
}
