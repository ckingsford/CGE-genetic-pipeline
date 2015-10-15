/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datareader.geneticreader;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;

/**
 * @author zinman
 *
 */
public abstract class AbstractGeneticFileDataReader extends AbstractDataReader {

	public AbstractGeneticFileDataReader() {
		((OptionsLoadGenetic) OptionsFactory.getOptions("loadGenetic")).registerFileReader(this, supportedExtensions());
	}

	public abstract String[] supportedExtensions();	
	
	/**
	 * @param filename
	 * @param dataFilters
	 * @return
	 */
	abstract public SamplesGeneticData read(String filename, ArrayList<AbstractDataFilter> dataFilters);


}
