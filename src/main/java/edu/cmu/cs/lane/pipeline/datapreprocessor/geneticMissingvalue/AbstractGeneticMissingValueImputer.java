package edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lane.datatypes.dataset.AbstractMatrixSampleDataType;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;

public abstract class AbstractGeneticMissingValueImputer {

//    public void imputeMissingValue(File inFile, File outFile, List<String> patientsToRemove);

	/**
	 * 
	 * @param data
	 * @return
	 */
	public abstract ArrayList<SamplesGeneticData> imputeMissingValue(ArrayList<SamplesGeneticData> data);
    
	/**
	 * 
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @param size
	 */
	public abstract void setWindowSize(int size);

}
