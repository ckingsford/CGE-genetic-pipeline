/**
 * 
 */
package edu.cmu.cs.lane.brokers.load;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.BatchInfo;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;

/**
 * Abastract class for loading genetic data
 * @author zinman
 *
 */
public abstract class AbstractLoadGeneticModule {
	abstract public String getName();
	abstract public boolean initialize();

	/**
	 * @param iDs
	 * @param filters
	 * @return
	 */
	abstract public ArrayList<SamplesGeneticData> loadData(ArrayList<BatchInfo> groupIds, ArrayList<AbstractDataFilter> filters); 

	abstract public ArrayList<SamplesGeneticData> loadData(int index, ArrayList<AbstractDataFilter> filters);

	abstract public ArrayList<ArrayList<BatchInfo>> getBatchIDs();
	
}
