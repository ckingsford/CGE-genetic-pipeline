package edu.cmu.cs.lane.pipeline.datareader.geneticreader;

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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.core.Is;
import org.jblas.DoubleMatrix;

import edu.cmu.cs.lane.brokers.ClinicalDictionary;
import edu.cmu.cs.lane.datatypes.dataset.SNProwDataBean;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.offlinetasks.FileHelperTask;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.filters.VariantFilterBean;
import edu.cmu.cs.lane.pipeline.datareader.filters.LookUpDataFilter;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.utils.ArrayIndexComparator;

public class SimpleTableReader extends AbstractGeneticFileDataReader {


	@Override
	public String getName() {
		return "SimpleTableReader";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#hasOrientationOptions()
	 */
	@Override
	public boolean hasOrientationOptions() {
		return true;
	}

	@Override
	public AbstractDataReader create() {
		return new SimpleTableReader();
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#supportedExtensions()
	 */
	@Override
	public String[] supportedExtensions() {
		String [] ext = {".tab",".tab.txt",".tsv"};
		return ext;
	}

	/*
	@Override
	public void initializeDataReader() throws Exception {
		// String config = "obesity-reader.properties";
		// PropertiesConfiguration props = new PropertiesConfiguration();
		// props.load(config);
	}*/
	
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#read(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public SamplesGeneticData read(String filename, ArrayList<AbstractDataFilter> dataFilters) {
		int  featuresInColumns = 0; //no data filters defined
		if (dataFilters!=null){
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputOrientation()
					.equalsIgnoreCase("rows-as-patients")) {
				featuresInColumns = 2; // columns-as-features
			}else{
				featuresInColumns = 1; // rows-as-features
			}
		}
		
		SamplesGeneticData samplesGeneticData = new SamplesGeneticData();
		Integer[] indices = null;
		ArrayIndexComparator comparator;
		try{
			BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			boolean[] rowsToKeep = null;
			boolean[] columnsToKeep = null;
			String line;
			int columns = -1;
			if  ((line = is.readLine()) != null) {
				String[] elements = line.split("\\s+");
				
				if (featuresInColumns == 2){
					//todo - require vector not handled in this case
					/*
					boolean requiresSnpVector = false; 
					for(AbstractDataFilter dataFilter : dataFilters){
						requiresSnpVector |= dataFilter.requiresSnpVector();
					}
					*/
					columnsToKeep = new boolean[elements.length];
					VariantFilterBean filterBean = new VariantFilterBean();
					ArrayList<String> featuresList = new ArrayList<String>();
					for (int i=1; i<elements.length; i++){ //skip header
						filterBean.setFullId(elements[i]);
						filterBean.setRsId(elements[i]);
						boolean useColumn = true;
						for(AbstractDataFilter dataFilter : dataFilters){
							useColumn = dataFilter.filter(filterBean, "");
							if (!useColumn) break;
						}
						if (useColumn){
							featuresList.add(elements[i]);
							columnsToKeep[i] = true;
						}else{
							columnsToKeep[i] = false;
						}
					}
					columns = featuresList.size();
					samplesGeneticData.setFeatureNames(featuresList); 
				}else{ // features in rows - set samplesNames
					columns = elements.length -1;
					
					String[] sNames = Arrays.copyOfRange(elements, 1, elements.length); 
					comparator = new ArrayIndexComparator(sNames); //sorting the samples
					indices = comparator.createIndexArray();
					Arrays.sort(indices, comparator);
					samplesGeneticData.setFeatureNames(comparator.sortByIndices(sNames, indices)); //actually sample names - output is always set such that in the output features are in columns - hence doing it in reverse
				}
			}
			is.close();

			int rows = FileHelperTask.getNumberOfLinesInFile(new InputStreamReader(new FileInputStream(filename)));
			rows --; //remove header

			int linesToRemoveCount = 0;
			if (featuresInColumns == 1){ //filter features in rows
				is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
				rowsToKeep = new boolean[rows+1];
				int lineIndex =0 ;
				String str;
				boolean requiresSnpVector = false;
				for(AbstractDataFilter dataFilter : dataFilters){
					requiresSnpVector |= dataFilter.requiresSnpVector();
				}
				while ((str = is.readLine()) != null) {
					if (lineIndex==0) {
						lineIndex++;
						continue;
					}
					boolean useLine = true;
					String[] elements = str.split("\\s+");
					VariantFilterBean filterBean = new VariantFilterBean();
					String featureId = elements[0];
					if (featureId.startsWith("chr")){ //remove "chr" if this was included in the beginning of the identifier
						featureId = featureId.substring(3);
					}
					
					filterBean.setRsId(featureId);
					filterBean.setFullId(featureId);
					if (requiresSnpVector){
						//create Snp vector
					}

					for(AbstractDataFilter dataFilter : dataFilters){
						useLine = dataFilter.filter(filterBean, str);
						if (!useLine) break;
					}
					rowsToKeep[lineIndex] = useLine;
					if (!useLine) linesToRemoveCount++;
					lineIndex ++;
				}
				is.close();
			}
			
			// Go through file a second time process the actual data.
			is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			samplesGeneticData.initializeDataOnly(rows-linesToRemoveCount, columns);

			int r = 0;
			int currentFeatureR = 0; //in case features are in rows
			while ((line = is.readLine()) != null) {
				if (r==0) {
					r++;
					continue;
				}
				String[] elements = line.split("\\s+");
				if (featuresInColumns == 0 || featuresInColumns == 2) { //features are in columns
					samplesGeneticData.setSampleName(r-1, elements[0]); 
					int currentFeatureC = 0;
					for (int c = 1; c < elements.length; c++){
						if (columnsToKeep==null || columnsToKeep[c]){
							samplesGeneticData.setData(r - 1, currentFeatureC, Double.valueOf(elements[c]));
							currentFeatureC++;
						}
					}
				}else{ //features in rows
					if (rowsToKeep[r]){
						String featureId = elements[0];
						if (featureId.startsWith("chr")){ //remove "chr" if this was included in the beginning of the identifier
							featureId = featureId.substring(3);
						}
						samplesGeneticData.setSampleName(currentFeatureR, featureId); //features are in rows - force it in reverse to put feature info in columns
						
						for (int c = 1; c < elements.length; c++){
							samplesGeneticData.setData(currentFeatureR, indices[c - 1], Double.valueOf(elements[c]));
						}
						currentFeatureR++;
					}
				}
				r++;
			}
			is.close();
			
			if (featuresInColumns == 2){
				//setting feature type to SNPId - output is always set such that in the output features are in columns
				for (int f=0; f<samplesGeneticData.getFeaturesCount();f++){
					samplesGeneticData.setExtendedFeatureInfo(f, ClinicalDictionary.getSNPIdType());
				}
			}else { //features are in rows - force it in reverse to put feature info in columns
				for (int f=0; f<samplesGeneticData.getSamplesCount();f++){
					samplesGeneticData.setExtendedSampleInfo(f, ClinicalDictionary.getSNPIdType());
				}
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			
		}
		return samplesGeneticData;
	}


}

