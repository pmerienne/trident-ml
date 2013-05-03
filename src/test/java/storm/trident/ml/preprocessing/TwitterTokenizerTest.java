package storm.trident.ml.preprocessing;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TwitterTokenizerTest {

	@Test
	public void testRemoveUsername() {
		// Given
		String tweet = "@PrincessSuperC Hey Cici";
		TwitterTokenizer tokenizer = new TwitterTokenizer();

		// When
		List<String> actualTokens = tokenizer.tokenize(tweet);

		// Then
		List<String> expectedTokens = Arrays.asList("hei", "cici");
		assertEquals(expectedTokens, actualTokens);
	}

	@Test
	public void testRemoveHashTagIndicator() {
		// Given
		String tweet = "arg #word!";
		TwitterTokenizer tokenizer = new TwitterTokenizer();

		// When
		List<String> actualTokens = tokenizer.tokenize(tweet);

		// Then
		List<String> expectedTokens = Arrays.asList("arg", "word");
		assertEquals(expectedTokens, actualTokens);
	}

	@Test
	public void testRemoveCharacterRepetitions() {
		// Given
		String tweet = "so huuunggryy!";
		TwitterTokenizer tokenizer = new TwitterTokenizer();

		// When
		List<String> actualTokens = tokenizer.tokenize(tweet);

		// Then
		List<String> expectedTokens = Arrays.asList("so", "hungri");
		assertEquals(expectedTokens, actualTokens);
	}

	@Test
	public void testRemoveWordsStartingNumber() {
		// Given
		String tweet = "it's 15PM we're in the 20th century";
		TwitterTokenizer tokenizer = new TwitterTokenizer();

		// When
		List<String> actualTokens = tokenizer.tokenize(tweet);

		// Then
		List<String> expectedTokens = Arrays.asList("we'r", "centuri");
		assertEquals(expectedTokens, actualTokens);
	}

	@Test
	public void testRemoveHTMLMarkum() {
		// Given
		String tweet = "fb &gt; tw";
		TwitterTokenizer tokenizer = new TwitterTokenizer();

		// When
		List<String> actualTokens = tokenizer.tokenize(tweet);

		// Then
		List<String> expectedTokens = Arrays.asList("fb", "tw");
		assertEquals(expectedTokens, actualTokens);
	}

	@Test
	public void testWithNGram() {
		// Given
		String tweet = "it's not bad movie";
		TwitterTokenizer tokenizer = new TwitterTokenizer(2, 2);

		// When
		List<String> actualTokens = tokenizer.tokenize(tweet);

		// Then
		List<String> expectedTokens = Arrays.asList("_ bad", "bad", "bad movi", "movi");
		assertEquals(expectedTokens, actualTokens);
	}
}
