/**
 * 
 */
package edu.cmu.cs.lane.datatypes.dataset;

import java.util.ArrayList;

/**
 * @author zinman
 *
 */
public class CVSamplesDatasetList {
	SamplesDatasetList training;
	SamplesDatasetList testing;
	
	/**
	 * 
	 */
	public CVSamplesDatasetList() {
		training = new SamplesDatasetList();
		testing = new SamplesDatasetList();
	}
	
	public void addTrainingDataset(SamplesDatasetList trainingSet) {
		training = trainingSet;
	}

	public void addTestingDataset(SamplesDatasetList testingSet) {
		testing = testingSet;
	}
	
	public SamplesDatasetList getTrainingSet() {
		return training;
	}
	public SamplesDatasetList getTestingSet() {
		return testing;
	}
}
