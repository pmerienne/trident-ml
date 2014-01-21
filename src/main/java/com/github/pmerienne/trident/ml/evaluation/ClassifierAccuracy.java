package com.github.pmerienne.trident.ml.evaluation;

public class ClassifierAccuracy<L> implements Evaluator<L> {

	private static final long serialVersionUID = 938679193655075913L;

	private final Long totalCount;
	private final Long errorCount;

	public ClassifierAccuracy() {
		this.totalCount = 0L;
		this.errorCount = 0L;
	}

	public ClassifierAccuracy(long totalCount, long errorCount) {
		this.totalCount = totalCount;
		this.errorCount = errorCount;
	}

	@Override
	public ClassifierAccuracy<L> update(L expected, L prediction) {
		boolean error = false;

		if (expected != null && prediction != null) {
			if (!expected.equals(prediction)) {
				error = true;
			}
		} else if (expected == null && prediction != null) {
			error = true;
		} else if (prediction == null && expected != null) {
			error = true;
		}

		return new ClassifierAccuracy<L>(totalCount + 1, error ? errorCount + 1 : errorCount);
	}

	@Override
	public double getEvaluation() {
		return errorCount.doubleValue() / totalCount.doubleValue();
	}

	@Override
	public long instanceCount() {
		return errorCount;
	}

}
