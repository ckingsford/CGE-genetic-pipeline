/**
 * 
 */
package edu.cmu.cs.lane.brokers.preprocessing;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;

/**
 * An abstract class to encapsulate preprocessing objects of clinical data
 * @author zinman
 *
 */
public abstract class AbstractCGEclinicalPreprocessingContorller {
	/**
	 * recommended guidelines:
	 * 1. check if possible to load a previous checkpoint
	 * 2. process
	 * 3. if defined - save checkpoint as tab format
	 * @param data
	 * @param loadStore 
	 * @return
	 */
	 abstract public ArrayList<SamplesLabels> apply (ArrayList<SamplesLabels> data, boolean loadStore, String title);

	/**
	 * @param data
	 * @param useCheckpoints
	 * @param title
	 * @return
	 */
	abstract public boolean wasApplied(String title); 
	
	abstract public String getName();
}
