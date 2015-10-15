/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datareader.clinicalreader;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsLoadClinical;

/**
 * @author zinman
 *
 */
public abstract class AbstractClinicalFileDataReader extends AbstractDataReader {

	public AbstractClinicalFileDataReader() {
		((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).registerFileReader(this, supportedExtensions());
	}

	public abstract String[] supportedExtensions();	
	
	/**
	 * @param filename
	 * @param dataFilters
	 * @return
	 */
	abstract public SamplesLabels read(String filename, boolean transpose);


}
