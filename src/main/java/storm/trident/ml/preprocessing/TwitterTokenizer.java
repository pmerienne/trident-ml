package storm.trident.ml.preprocessing;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class TwitterTokenizer extends EnglishTokenizer {

	private static final long serialVersionUID = -2486285775626564821L;

	private final static String URL_REGEX = "((www\\.[\\s]+)|(https?://[^\\s]+))";
	private final static String CONSECUTIVE_CHARS = "([a-z])\\1{1,}";
	private final static String STARTS_WITH_NUMBER = "[1-9]\\s*(\\w+)";

	public TwitterTokenizer() {
		super();
	}

	public TwitterTokenizer(int minNGram, int maxNGram) {
		super(minNGram, maxNGram);
	}

	@Override
	public List<String> tokenize(String text) {
		text = this.preprocess(text);
		return super.tokenize(text);
	}

	protected String preprocess(String tweet) {
		// Remove urls
		tweet = tweet.replaceAll(URL_REGEX, "");

		// Remove @username
		tweet = tweet.replaceAll("@([^\\s]+)", "");

		// Remove character repetition
		tweet = tweet.replaceAll(CONSECUTIVE_CHARS, "$1");

		// Remove words starting with a number
		tweet = tweet.replaceAll(STARTS_WITH_NUMBER, "");

		// Escape HTML
		tweet = tweet.replaceAll("&amp;", "&");
		tweet = StringEscapeUtils.unescapeHtml(tweet);

		return tweet;
	}

}
