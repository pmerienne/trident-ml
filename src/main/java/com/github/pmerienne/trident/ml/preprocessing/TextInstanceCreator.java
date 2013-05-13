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
package com.github.pmerienne.trident.ml.preprocessing;

import java.util.List;

import com.github.pmerienne.trident.ml.core.TextInstance;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class TextInstanceCreator<L> extends BaseFunction {

	private static final long serialVersionUID = 3312351524410720639L;

	private boolean withLabel = true;
	private TextTokenizer textAnalyser = new EnglishTokenizer();

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
		String text = tuple.getString(this.withLabel ? 1 : 0);

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
