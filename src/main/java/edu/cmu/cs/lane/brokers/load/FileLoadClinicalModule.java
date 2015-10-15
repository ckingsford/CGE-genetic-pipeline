/**
 * 
 */
package edu.cmu.cs.lane.brokers.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jblas.DoubleMatrix;
import org.jblas.ranges.RangeUtils;

import edu.cmu.cs.lane.datatypes.dataset.MatrixLabels;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.matrixmath.MatrixUtils;
import edu.cmu.cs.lane.offlinetasks.FileHelperTask;
import edu.cmu.cs.lane.pipeline.datareader.clinicalreader.AbstractClinicalFileDataReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.AbstractGeneticFileDataReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadClinical;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;

/**
 * @author zinman
 *
 */
public class FileLoadClinicalModule extends AbstractLoadClinicalModule {

	/**
	 * @see edu.cmu.cs.lane.brokers.load.AbstractLoadClinicalModule#getName()
	 */
	@Override
	public String getName() {
		return "file";
	}
	
	@Override 
	public ArrayList<SamplesLabels> load() {
		ArrayList<SamplesLabels> samplesLabelsList = new ArrayList<SamplesLabels>();
		
		ArrayList<String> filesToLoad = new ArrayList<String>();
		ArrayList<Boolean> transposeList = new ArrayList<Boolean>();

		String folder = ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputFolder();
		String yFile = folder + ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputFilePattern();
		
		boolean doTranspose = false;
		if (/*reader has orientation && */
			((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputOrientation() != null
			&& !((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputOrientation().equalsIgnoreCase("rows-as-patients")) {
			doTranspose = true;
	    }
		filesToLoad.add(yFile);
		transposeList.add(doTranspose);
		

		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground()){
			String yFolderBG = ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getBackgroundClinicalInputFolder();
			yFile = yFolderBG + File.separator + ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getBackgroundClinicalInputFilePattern();
			
			doTranspose = false;
			if (/*reader has orientation && */
				((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getBackgroundClinicalInputOrientation() != null
				&& !((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getBackgroundClinicalInputOrientation().equalsIgnoreCase("rows-as-patients")) {
				doTranspose = true;
		    }
			filesToLoad.add(yFile);
			transposeList.add(doTranspose);
			filesToLoad.add(yFile);
			transposeList.add(false);
		}
		
		//Loading Clinical
		for (int dataset = 0; dataset < filesToLoad.size(); dataset++) {
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.print("\tLoading clinical info: " + filesToLoad.get(dataset));
			
			String filename = filesToLoad.get(dataset);
			AbstractClinicalFileDataReader reader = getReader(filename);	
			SamplesLabels labels = reader.read(filename, transposeList.get(dataset));
					
			samplesLabelsList.add(labels);			
		}	

		return samplesLabelsList;
	}
	
	private AbstractClinicalFileDataReader getReader(String filename){
		return ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getReaderByFilename(filename);
	}
}
