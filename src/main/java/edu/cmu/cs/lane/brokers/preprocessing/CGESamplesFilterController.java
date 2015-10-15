/**
 * 
 */
package edu.cmu.cs.lane.brokers.preprocessing;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.AbstractMatrixSampleDataType;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;

/**
 * Abstract controller for samples
 * @author zinman
 *
 */
public class CGESamplesFilterController extends AbstractCGEgeneticPreprocessingContorller{
	/**
	 * @see edu.cmu.cs.lane.brokers.preprocessing.AbstractCGEgeneticPreprocessingContorller#getName()
	 */
	@Override
	public String getName() {
		return "samplesFilter";
	}

	
	/**
	 * @see edu.cmu.cs.lane.brokers.preprocessing.AbstractCGEgeneticPreprocessingContorller#wasApplied(java.lang.String)
	 */
	@Override
	public boolean wasApplied(String title) {
		return false;
	}
	
	@Override
	public ArrayList<SamplesGeneticData> apply (ArrayList<SamplesGeneticData> data, boolean loadStore, String title){
		if (loadStore == true){
			//load from check point
		}
		
		//process
		
		if (loadStore == true){
			//store check point
		}
		return data;
	}



}
