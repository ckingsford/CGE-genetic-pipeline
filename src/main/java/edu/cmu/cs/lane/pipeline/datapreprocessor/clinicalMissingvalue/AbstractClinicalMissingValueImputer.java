package edu.cmu.cs.lane.pipeline.datapreprocessor.clinicalMissingvalue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lane.datatypes.dataset.AbstractMatrixSampleDataType;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;

public abstract class AbstractClinicalMissingValueImputer {

//    public void imputeMissingValue(File inFile, File outFile, List<String> patientsToRemove);

	/**
	 * 
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public abstract ArrayList<SamplesLabels> imputeMissingValue(ArrayList<SamplesLabels> data);
    

	

}
