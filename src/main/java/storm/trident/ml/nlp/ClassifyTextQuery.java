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
package storm.trident.ml.nlp;

import java.util.ArrayList;
import java.util.List;

import storm.trident.ml.core.TextInstance;
import storm.trident.ml.util.KeysUtil;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class  ClassifyTextQuery<L> extends BaseQueryFunction<MapState<TextClassifier<L>>, L> {

	private static final long serialVersionUID = -9046858936834644113L;

	private String classifierName;

	public ClassifyTextQuery(String classifierName) {
		this.classifierName = classifierName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<L> batchRetrieve(MapState<TextClassifier<L>> state, List<TridentTuple> tuples) {
		List<L> labels = new ArrayList<L>();

		List<TextClassifier<L>> classifiers = state.multiGet(KeysUtil.toKeys(this.classifierName));
		if (classifiers != null && !classifiers.isEmpty()) {
			TextClassifier<L> classifier = classifiers.get(0);

			L label;
			TextInstance<L> instance;
			for (TridentTuple tuple : tuples) {
				instance = (TextInstance<L>) tuple.get(0);
				label = classifier.classify(instance.tokens);
				labels.add(label);
			}
		}

		return labels;
	}

	public void execute(TridentTuple tuple, L result, TridentCollector collector) {
		collector.emit(new Values(result));
	}

}
