/**
 * 
 */
package edu.cmu.cs.lane.brokers.load;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;

/**
 * An abastract class for loading clinical information
 * @author zinman
 *
 */
public abstract class AbstractLoadClinicalModule {
	abstract  public String getName();

	/**
	 * @return
	 */
	public ArrayList<SamplesLabels> load() {
		return null;
	}
}
