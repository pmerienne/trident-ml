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
package com.github.pmerienne.trident.ml.nlp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TwitterSentimentClassifierTest {

	@Test
	public void testWithSomeTwitterSentiments() {
		TwitterSentimentClassifier classifier = new TwitterSentimentClassifier();

		test(false, "RT @JazminBianca: I hate Windows 8. I hate Windows 8. I hate Windows 8.", classifier);
		test(false, "I don't like Windows 8, I think it's overrated =))", classifier);
		test(false, "Windows 8 is stupid as fuck ! Shit is confusing <<", classifier);
		test(false, "not a big fan of Windows 8", classifier);
		test(false, "Forever hating apple for changing the chargers #wanks", classifier);
		test(false, "#CSRBlast #CSRBlast That moment you pull out a book because the customer service at apple is horrible and takes wa... http://t.co/WxqyGR9a85", classifier);

		test(true, "Windows 8 is awesome :D", classifier);
		test(true, "God Windows 8 is amazing. Finally", classifier);
		test(true, "Register for the AWESOME Windows 8 western US regional events all in the next few weeks! http://t.co/7lfqaHSxfs #w8appfactor @w8appfactor", classifier);
		test(true, "Windows 8 is fun to use. I like it better then mac lion.", classifier);
		test(true, "Good morning loves 😁😁 apple jacks doe http://t.co/nOfi42enoQ", classifier);
		test(true, "@Saad_khan33 No i prefer apple anyday", classifier);
	}

	protected void test(boolean expected, String text, TwitterSentimentClassifier classifier) {
		boolean actual = classifier.classify(text);
		assertEquals("Expecting " + expected + " but was " + actual + " for " + text, expected, actual);
	}
}
