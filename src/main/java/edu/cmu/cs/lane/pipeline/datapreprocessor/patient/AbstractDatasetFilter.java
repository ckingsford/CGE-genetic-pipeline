/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datapreprocessor.patient;

import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;

/**
 * @author zinman
 *
 */
public abstract class AbstractDatasetFilter {
	
	abstract public String getName();
	abstract public SamplesDataset filter(SamplesDataset dataset);
}
