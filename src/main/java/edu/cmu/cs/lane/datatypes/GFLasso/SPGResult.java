package edu.cmu.cs.lane.datatypes.GFLasso;

import org.jblas.DoubleMatrix;

public class SPGResult {
	/*
	 * @param Y
	 *            multi-task outputs
	 * @param X
	 *            input design matrix
	 * @param gamma
	 *            regularization for group norm
	 * @param lambda
	 *            regularization for L1 penalty
	 * @param C
	 *            sum_|g| by |E|
	 * @param CNorm
	 *            ||C||^2
	 * @param option
	 *            maxiter; tol, b0 ;
	 */

	DoubleMatrix beta;

	public DoubleMatrix getBeta() {
		return beta;
	}

	public void setBeta(DoubleMatrix beta) {
		this.beta = beta;
	}

	public SPGResult(DoubleMatrix beta) {
		this.beta = beta;
	}


}
