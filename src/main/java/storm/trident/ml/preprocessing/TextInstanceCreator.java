package storm.trident.ml.preprocessing;

import java.util.List;

import storm.trident.ml.core.TextInstance;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class TextInstanceCreator<L> extends BaseFunction {

	private static final long serialVersionUID = 3312351524410720639L;

	private boolean withLabel = true;
	private TextTokenizer textAnalyser = new TextTokenizer();

	public TextInstanceCreator() {
	}

	public TextInstanceCreator(boolean withLabel) {
		this.withLabel = withLabel;
	}

	public TextInstanceCreator(boolean withLabel, TextTokenizer textAnalyser) {
		this.withLabel = withLabel;
		this.textAnalyser = textAnalyser;
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		TextInstance<L> instance = this.createInstance(tuple);
		collector.emit(new Values(instance));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected TextInstance<L> createInstance(TridentTuple tuple) {
		L label = this.withLabel ? (L) tuple.get(0) : null;
		String text = tuple.getString(1);

		List<String> tokens = this.extractTokens(text);

		TextInstance<L> instance = new TextInstance(label, tokens);
		return instance;
	}

	protected List<String> extractTokens(String text) {
		List<String> tokens = this.textAnalyser.tokenize(text);
		return tokens;
	}

	public boolean isWithLabel() {
		return withLabel;
	}

	public void setWithLabel(boolean withLabel) {
		this.withLabel = withLabel;
	}

	public TextTokenizer getTextAnalyser() {
		return textAnalyser;
	}

	public void setTextAnalyser(TextTokenizer textAnalyser) {
		this.textAnalyser = textAnalyser;
	}

}
