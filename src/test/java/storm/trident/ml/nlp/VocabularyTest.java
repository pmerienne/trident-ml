package storm.trident.ml.nlp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class VocabularyTest {

	@Test
	public void testCount() {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.add("only one");
		vocabulary.add("we are 2");
		vocabulary.add("we are 2");

		assertEquals(1, vocabulary.count("only one").intValue());
		assertEquals(2, vocabulary.count("we are 2").intValue());
		assertEquals(0, vocabulary.count("I'm not here").intValue());
	}

	@Test
	public void testFrequency() {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.add("only one");
		vocabulary.add("we are 2");
		vocabulary.add("we are 2");
		vocabulary.add("I like kitten");

		assertEquals(0.25, vocabulary.frequency("only one"), 0.001);
		assertEquals(0.5, vocabulary.frequency("we are 2"), 0.001);
		assertEquals(0.0, vocabulary.frequency("I'm not here"), 0.001);
	}

	@Test
	public void testAddAll() {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.addAll(Arrays.asList("only one", "we are 2", "we are 2"));

		assertEquals(1, vocabulary.count("only one").intValue());
		assertEquals(2, vocabulary.count("we are 2").intValue());
		assertEquals(0, vocabulary.count("I'm not here").intValue());
	}

	@Test
	public void testLimit() {
		// Given
		Vocabulary vocabulary = new Vocabulary();
		for (int i = 1; i < 6; i++) {
			for (int j = 0; j < i; j++) {
				vocabulary.add("we are " + i);
			}
		}

		// When
		vocabulary.limitWords(3);

		// Then
		assertEquals(3, vocabulary.wordCount().intValue());
		assertEquals(0, vocabulary.count("we are 1").intValue());
		assertEquals(0, vocabulary.count("we are 2").intValue());
		assertEquals(3, vocabulary.count("we are 3").intValue());
		assertEquals(4, vocabulary.count("we are 4").intValue());
		assertEquals(5, vocabulary.count("we are 5").intValue());

	}
}
