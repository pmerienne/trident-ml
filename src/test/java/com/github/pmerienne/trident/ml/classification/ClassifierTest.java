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
package com.github.pmerienne.trident.ml.classification;

import java.util.List;

import com.github.pmerienne.trident.ml.classification.Classifier;
import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.testing.data.DatasetUtils;


public abstract class ClassifierTest {

	private final static Integer FOLD_NB = 10;

	/**
	 * Cross validation with 10 folds
	 * 
	 * @param <L>
	 * @param <F>
	 * @param classifier
	 * @param samples
	 * @return
	 */
	protected <L> double eval(Classifier<L> classifier, List<Instance<L>> samples) {
		double error = 0.0;

		for (int i = 0; i < FOLD_NB; i++) {
			List<Instance<L>> training = DatasetUtils.getTrainingFolds(i, FOLD_NB, samples);
			List<Instance<L>> eval = DatasetUtils.getEvalFold(i, FOLD_NB, samples);
			error += this.eval(classifier, training, eval);
		}

		return error / FOLD_NB;
	}

	protected <L> double eval(Classifier<L> classifier, List<Instance<L>> training, List<Instance<L>> eval) {
		classifier.reset();

		// Train
		for (Instance<L> sample : training) {
			classifier.update(sample.label, sample.features);
		}

		// Evaluate
		double errorCount = 0.0;
		L actualLabel;
		for (Instance<L> sample : eval) {
			actualLabel = classifier.classify(sample.features);
			if (!sample.label.equals(actualLabel)) {
				errorCount++;
			}
		}

		return errorCount / eval.size();
	}

}
