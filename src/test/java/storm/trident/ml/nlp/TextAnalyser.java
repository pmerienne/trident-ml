package storm.trident.ml.nlp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

public class TextAnalyser {

	private static final Log LOGGER = LogFactory.getLog(TextAnalyser.class);

	private final static Version LUCENE_VERSION = Version.LUCENE_35;
	private final static Analyzer ANALYZER = new EnglishAnalyzer(LUCENE_VERSION);

	public static List<String> parse(String text) {
		List<String> words = new ArrayList<String>();
		if (text != null && !text.isEmpty()) {
			try {
				Reader reader = new StringReader(text);

				TokenStream tokenStream = ANALYZER.tokenStream(null, reader);
				OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
				CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

				while (tokenStream.incrementToken()) {
					offsetAttribute.startOffset();
					offsetAttribute.endOffset();
					String term = charTermAttribute.toString();
					words.add(term);
				}
			} catch (IOException ioe) {
				LOGGER.error("Unable to analyze text : " + ioe.getMessage(), ioe);
			}
		}

		return words;
	}
}
