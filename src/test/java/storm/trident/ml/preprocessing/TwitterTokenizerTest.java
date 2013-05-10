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
