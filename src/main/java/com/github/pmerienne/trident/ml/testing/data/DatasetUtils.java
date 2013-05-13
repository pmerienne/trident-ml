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
package com.github.pmerienne.trident.ml.testing.data;

import java.util.ArrayList;
import java.util.List;

public class DatasetUtils {

	public static <E> List<E> getEvalFold(int foldIndex, int foldNb, List<E> samples) {
		List<E> eval = new ArrayList<E>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i >= start && i < end) {
				eval.add(samples.get(i));
			}
		}

		return eval;
	}

	public static <E> List<E> getTrainingFolds(int foldIndex, int foldNb, List<E> samples) {
		List<E> train = new ArrayList<E>();

		int start = foldIndex * (samples.size() / foldNb);
		int end = (foldIndex + 1) * (samples.size() / foldNb);

		for (int i = 0; i < samples.size(); i++) {
			if (i < start || i >= end) {
				train.add(samples.get(i));
			}
		}

		return train;
	}
}
