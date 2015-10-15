package edu.cmu.cs.lane.algorithmicEngine;

import org.jblas.DoubleMatrix;

/**
 * a wrapper calls on DoubleMatrix
 * @author zinman
 *
 */
public class SimulatedData {
	public DoubleMatrix Y;
	public DoubleMatrix X;
	public DoubleMatrix B;
	public DoubleMatrix getY() {
		return Y;
	}
	public void setY(DoubleMatrix y) {
		Y = y;
	}
	public DoubleMatrix getX() {
		return X;
	}
	public void setX(DoubleMatrix x) {
		X = x;
	}
	public DoubleMatrix getB() {
		return B;
	}
	public void setB(DoubleMatrix b) {
		B = b;
	}
	public SimulatedData(DoubleMatrix y, DoubleMatrix x, DoubleMatrix b) {
		super();
		Y = y;
		X = x;
		B = b;
	}
}
