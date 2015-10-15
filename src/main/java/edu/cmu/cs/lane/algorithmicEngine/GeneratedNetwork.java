package edu.cmu.cs.lane.algorithmicEngine;

import org.jblas.DoubleMatrix;

import edu.cmu.cs.lane.datatypes.GFLasso.Coordinate;

/**
 * helper glass for GFLasso
 * @author zinman
 *
 */

public class GeneratedNetwork {
	/**
	 * C: is the E by V incident matrix as in the paper CNorm in Proposition 2
	 * in the paper E is E by 2 matrix E(i,1) E(i,2) forms an edge Ecoef:
	 * \tau(r_{ml}) for each edge e=(m,l) Esign: sign(r_{ml}) for each edge
	 * e=(m,l) R: correlation matrix
	 */
	// We actually only use C and CNorm so no need for the rest
	public DoubleMatrix C;
	public double CNorm;
	public Coordinate[] E;
	public DoubleMatrix S;

	public GeneratedNetwork(DoubleMatrix c, double cNorm) {
		super();
		C = c;
		CNorm = cNorm;
	}

	public GeneratedNetwork(DoubleMatrix c, double cNorm, Coordinate[] e,
			DoubleMatrix s) {
		super();
		C = c;
		CNorm = cNorm;
		E = e;
		S = s;
	}
	
	/**
	 * A set of helper functions to interact with the GFLasso network
	 * @return
	 */

	public DoubleMatrix getC() {
		return C;
	}

	public void setC(DoubleMatrix c) {
		C = c;
	}

	public double getCNorm() {
		return CNorm;
	}

	public void setCNorm(double cNorm) {
		CNorm = cNorm;
	}

	public Coordinate[] getE() {
		return E;
	}

	public void setE(Coordinate[] e) {
		E = e;
	}

	public DoubleMatrix getS() {
		return S;
	}

	public void setS(DoubleMatrix s) {
		S = s;
	}

}