package edu.cmu.cs.lane.datatypes.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.jblas.DoubleMatrix;


/**
 * The SamplesGeneticData class represents genetic data of one or more samples (patients).
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 */
public class SamplesGeneticData extends AbstractMatrixSampleDataType{

	/**
	 * 
	 */
	public SamplesGeneticData() {
		
	}
	
	/**
	 * 
	 */
	public SamplesGeneticData(AbstractMatrixSampleDataType o) {
		this.data = o.data;
		this.labels = o.labels;
	}
	
}
