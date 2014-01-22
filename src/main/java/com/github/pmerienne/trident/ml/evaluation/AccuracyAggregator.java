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
package com.github.pmerienne.trident.ml.evaluation;

import java.util.Objects;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

import com.github.pmerienne.trident.ml.core.Instance;
import com.github.pmerienne.trident.ml.evaluation.AccuracyAggregator.AccuracyState;

@SuppressWarnings("unchecked")
public class AccuracyAggregator<L> implements CombinerAggregator<AccuracyState<L>> {

	private static final long serialVersionUID = 1136784137149485843L;

	@Override
	public AccuracyState<L> init(TridentTuple tuple) {
		Instance<L> instance = (Instance<L>) tuple.getValue(0);
		L prediction = (L) tuple.getValue(1);
		L expected = instance.getLabel();

		boolean equals = Objects.equals(expected, prediction);

		AccuracyState<L> state = new AccuracyState<L>(1, equals ? 0 : 1);
		return state;
	}

	@Override
	public AccuracyState<L> combine(AccuracyState<L> val1, AccuracyState<L> val2) {
		return new AccuracyState<L>(val1.totalCount + val2.totalCount, val1.errorCount + val2.errorCount);
	}

	@Override
	public AccuracyState<L> zero() {
		return new AccuracyState<L>();
	}

	public static class AccuracyState<L> implements Evaluator<L> {

		private static final long serialVersionUID = 938679193655075913L;

		private final Long totalCount;
		private final Long errorCount;

		public AccuracyState() {
			this.totalCount = 0L;
			this.errorCount = 0L;
		}

		public AccuracyState(long totalCount, long errorCount) {
			this.totalCount = totalCount;
			this.errorCount = errorCount;
		}

		@Override
		public double getEvaluation() {
			return 1 - errorCount.doubleValue() / totalCount.doubleValue();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((errorCount == null) ? 0 : errorCount.hashCode());
			result = prime * result + ((totalCount == null) ? 0 : totalCount.hashCode());
			return result;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AccuracyState other = (AccuracyState) obj;
			if (errorCount == null) {
				if (other.errorCount != null)
					return false;
			} else if (!errorCount.equals(other.errorCount))
				return false;
			if (totalCount == null) {
				if (other.totalCount != null)
					return false;
			} else if (!totalCount.equals(other.totalCount))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "AccuracyState [totalCount=" + totalCount + ", errorCount=" + errorCount + "]";
		}

	}

}