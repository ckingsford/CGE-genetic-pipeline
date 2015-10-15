package edu.cmu.cs.lane.pipeline.datareader.clinicalreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.offlinetasks.FileHelperTask;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.AbstractGeneticFileDataReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.utils.ArrayIndexComparator;

public class ClinicalTableReader extends AbstractClinicalFileDataReader {

	@Override
	public String getName() {
		return "ClinicalTableReader";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#hasOrientationOptions()
	 */
	@Override
	public boolean hasOrientationOptions() {
		return true;
	}

	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#supportedExtensions()
	 */
	@Override
	public String[] supportedExtensions() {
		String [] ext = {".cli",".cli.txt"};
		return ext;
	}

	@Override
	public AbstractDataReader create() {
		// if propertiesFile is null read default
		return new ClinicalTableReader();
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.clinicalreader.AbstractClinicalFileDataReader#read(java.lang.String)
	 */
	@Override
	public SamplesLabels read(String filename, boolean transpose) {
		BufferedReader is;
		SamplesLabels samplesLabels = new SamplesLabels();
		try {
			is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
	
	
			String line;
	//		int rows = 0;
			int columns = -1;
			
			try {
				if  ((line = is.readLine()) != null) {

					String[] elements = line.split("\\s+");
					int numElements = elements.length;
					if (elements[0].length() == 0) {
						numElements--;
					}
					if (elements[elements.length - 1].length() == 0) {
						numElements--;
					}
					numElements--;

					if (columns == -1) {
						columns = numElements;
					} else {
						if (columns != numElements) {
							System.err.println("Number of elements changes in line " + line + ".");
						}
					}

					
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			is.close();
			
			int rows = FileHelperTask.getNumberOfLinesInFile(new InputStreamReader(new FileInputStream(filename)));
			rows --;
			
			
			samplesLabels.initialize(rows, columns);
			
			// Go through file a second time process the actual data.
			is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			int r = 0;
			Integer[] indices = null;
			ArrayIndexComparator comparator;
			while ((line = is.readLine()) != null) {
				if (r == 0) {
					r++;
					String[] sNames = Arrays.copyOfRange(line.split("\\s+"), 1, columns+1); //before transpose
					comparator = new ArrayIndexComparator(sNames); //sorting the samples
					indices = comparator.createIndexArray();
					Arrays.sort(indices, comparator);
					samplesLabels.setFeatureNames(comparator.sortByIndices(sNames, indices));
					continue;
				}
				String[] elements = line.split("\\s+");
				samplesLabels.setSampleName(r-1, elements[0]);
				for (int c = 1; c < elements.length; c++){
					samplesLabels.setData(r - 1, indices[c - 1], Double.valueOf(elements[c]));  //by sorted samples
				}
				r++;
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if (transpose){
			samplesLabels.transpose();
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())	{			
				System.out.println("\tColumns as Patients selected. Transposing matrices.");
			}
		} 	
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tClinical data: (samples)" + samplesLabels.getSamplesCount() + " x (features)" + samplesLabels.getFeaturesCount());

		return samplesLabels;

	}
}
