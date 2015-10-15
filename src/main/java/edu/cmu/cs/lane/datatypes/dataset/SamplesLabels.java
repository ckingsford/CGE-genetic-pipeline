package edu.cmu.cs.lane.datatypes.dataset;

import java.io.IOException;

import org.jblas.DoubleMatrix;

import edu.cmu.cs.lane.matrixmath.MatrixUtils;


/**
 * 
 * SamplesLabels is the base class that represents the clinical variables or sample labels (.e.g., disease state). 
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 * 
 * @see GFLassoDataLabels
 */
public class SamplesLabels  extends AbstractMatrixSampleDataType{
	/**
	 * 
	 */
	public SamplesLabels() {
		
	}
	
	/**
	 * 
	 */
	public SamplesLabels(AbstractMatrixSampleDataType o) {
		this.data = o.data;
		this.labels = o.labels;
	}
}
