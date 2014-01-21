package com.github.pmerienne.trident.ml.evaluation;

import java.io.Serializable;

public interface Evaluator<L> extends Serializable {

	Evaluator<L> update(L expected, L prediction);

	double getEvaluation();

	long instanceCount();

}