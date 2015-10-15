/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datareader.clinicalreader;

import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;

/**
 * @author zinman
 *
 */
public class ARFFReader extends AbstractClinicalFileDataReader {

	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#getName()
	 */
	@Override
	public String getName() {
		return "ARFFReader";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.clinicalreader.AbstractClinicalFileDataReader#supportedExtensions()
	 */
	@Override
	public String[] supportedExtensions() {
		String[] ext = {".arff", ".arff.txt"};
		return ext;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#create()
	 */
	@Override
	public AbstractDataReader create() {
		return new ARFFReader();
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#hasOrientationOptions()
	 */
	@Override
	public boolean hasOrientationOptions() {
		return false;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.clinicalreader.AbstractClinicalFileDataReader#read(java.lang.String, boolean)
	 */
	@Override
	public SamplesLabels read(String filename, boolean transpose) {
		// TODO Auto-generated method stub
		return null;
	}
}
