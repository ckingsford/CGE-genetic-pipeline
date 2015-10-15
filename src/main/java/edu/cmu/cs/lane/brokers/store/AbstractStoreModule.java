/**
 * 
 */
package edu.cmu.cs.lane.brokers.store;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;

/**
 * @author zinman
 *
 */
abstract public class AbstractStoreModule {
	abstract  public String getName();

	/**
	 * @param model
	 * @param analyzer
	 * @return 
	 */
	abstract public int storeModel(CGEModel model);

	/**
	 * @param analysesIds
	 * @return 
	 */
	abstract public int storeSet(ArrayList<Integer> analysesIds, AnalysisSetDetails analysisSetDetails);
}
