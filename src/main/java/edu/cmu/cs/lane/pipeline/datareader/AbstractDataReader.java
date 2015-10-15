package edu.cmu.cs.lane.pipeline.datareader;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;

abstract public class AbstractDataReader implements
		DataReaderPhaseFileInteractor<AbstractDataReader> {
	// String DIR="";
	// String FILE_FILTER_PATTERN="";
	
	/**
	 * 
	 */
	public AbstractDataReader() {
	}

	@Override
	public abstract String getName();
	
	@Override
	public abstract AbstractDataReader create();
	
	public abstract boolean hasOrientationOptions();

}
