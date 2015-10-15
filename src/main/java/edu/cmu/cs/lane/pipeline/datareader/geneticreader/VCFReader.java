package edu.cmu.cs.lane.pipeline.datareader.geneticreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.jblas.DoubleMatrix;

import edu.cmu.cs.lane.brokers.ClinicalDictionary;
import edu.cmu.cs.lane.datatypes.dataset.SampleGeneticBean;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.offlinetasks.FileHelperTask;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.filters.VariantFilterBean;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;
import edu.cmu.cs.lane.utils.ArrayIndexComparator;

/**
 * 
 * 
 * 
 */
public class VCFReader extends AbstractGeneticFileDataReader {
	
	static boolean initialized = false;
	
	@Override
	public String getName() {
		return "VCFReader";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#hasOrientationOptions()
	 */
	@Override
	public boolean hasOrientationOptions() {
		return false;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader#supportedExtensions()
	 */
	@Override
	public String[] supportedExtensions() {
		String [] ext = {".vcf",".vcf.gz",".vcf.txt"};
		return ext;
	}
	
	@Override
	public AbstractDataReader create() {
		// if propertiesFile is null read default
		return new VCFReader();
	}

	public VCFReader() {
		try {			
			// filterIds=LookUpDataFilter.loadLookUpFilter(LOOKUP_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param filename
	 * @param dataFilters
	 * @return output formatted as rows-as-patients
	 */
	@Override 
	public SamplesGeneticData read(String filename, ArrayList<AbstractDataFilter> dataFilters ) {
		SamplesGeneticData geneticData = new SamplesGeneticData();	
		
		boolean zipped = false;
		File file = new File(filename);
		if (filename.toString().toLowerCase().endsWith(".gz")) zipped = true;
		
		FileInputStream fileInputStream = null;
		try {
			BufferedReader bfr = null;	
			
			//get number of lines in the file
			fileInputStream = new FileInputStream(file);
			InputStream inputStream = (zipped?new GZIPInputStream(fileInputStream):fileInputStream);
			int numberOfLines = FileHelperTask.getNumberOfLinesInFile(new InputStreamReader(inputStream, "UTF-8"));		
			inputStream.close();
			fileInputStream.close();

			String str;
			int linesToRemoveCount = 0;
			
			boolean[] linesToKeep = null;
			if (dataFilters != null){
				linesToKeep = new boolean [numberOfLines];
				fileInputStream = new FileInputStream(file);
				inputStream = (zipped?new GZIPInputStream(fileInputStream):fileInputStream);
				bfr = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				int lineIndex =0 ;
				boolean requiresSnpVector = false;
				for(AbstractDataFilter dataFilter : dataFilters){
					requiresSnpVector |= dataFilter.requiresSnpVector();
				}

				while ((str = bfr.readLine()) != null) {
					str = str.trim();
					if (str.equals("") || str.startsWith("#")) {
						linesToKeep[lineIndex] = false;
						continue;
					}
					boolean useLine = true;
					VariantFilterBean filterBean = new VariantFilterBean();
					String rec[] = str.split("[\\t]");				
					filterBean.setRsId(rec[2]);
					filterBean.setFullId(rec[0] + "." + rec[1]);

					if (requiresSnpVector){
						//TODO: create Snp vector
					}

					for(AbstractDataFilter dataFilter : dataFilters){
						useLine = dataFilter.filter(filterBean, str);
						if (!useLine) break;
					}
					linesToKeep[lineIndex] = useLine;
					if (!useLine) linesToRemoveCount++;
					lineIndex ++;
				}
				bfr.close();
				inputStream.close();
				fileInputStream.close();
			}			
			
			fileInputStream = new FileInputStream(file);
			inputStream = (zipped?new GZIPInputStream(fileInputStream):fileInputStream);
			bfr = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			int headerLines = 0;
			int currentSnpIndex = 0;
			int lineIndex = 0;
			final int patientsStartColumn = 9;
			Integer[] indices = null;
			ArrayIndexComparator comparator;
			while ((str = bfr.readLine()) != null) {
				str = str.trim();
				if (str.equals("") || str.startsWith("##")) {
					headerLines ++;
					continue;
				}
				// headerline - setting samples in rows;
				if (str.startsWith("#")) { 
					headerLines ++;
					String[] rec = str.split("\\t"); // VCFheader line
					
					int featuresCount = numberOfLines-headerLines-linesToRemoveCount;
					int samplesCount = rec.length - patientsStartColumn + 1;
					
					String[] sNames = Arrays.copyOfRange(rec, patientsStartColumn, rec.length); 
					comparator = new ArrayIndexComparator(sNames); //sorting the samples
					indices = comparator.createIndexArray();
					Arrays.sort(indices, comparator);
					geneticData.initialize(comparator.sortByIndices(sNames, indices), featuresCount); // getting all the samples names (starting from column 9)

					continue;
				}
				//setting SNP info
				if (linesToKeep == null || linesToKeep[lineIndex]) {
					String rec[] = str.split("[\\t]");		
					geneticData.setFeatureName(currentSnpIndex, rec[0] + "." + rec[1]);  //chr.position
					geneticData.setExtendedFeatureInfo(currentSnpIndex, rec[2]); //rsID
					

					//setting the genetic data matrix
					for (int i = patientsStartColumn; i < rec.length; i++) {
						geneticData.setData(indices[i-patientsStartColumn], currentSnpIndex, getSNPinfo(rec[i])); //by sorted indices
					}
					currentSnpIndex++;
				}
				lineIndex++;
			}
			
			bfr.close();
			fileInputStream.close();
			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		//setting feature type to SNPId
		for (int f=0; f<geneticData.getFeaturesCount();f++){
			geneticData.setExtendedFeatureInfo(f, ClinicalDictionary.getSNPIdType());
		}

		return geneticData;
	}	
	
	private Byte getSNPinfo(String token){
		Byte snp = 3;
		String[] varInfo = null;
		String GT_phase1, GT_phase2 = null;
		if (token.contains("|")) {
			varInfo = token.split("[|]");
			String secondTerm = varInfo[1];
			GT_phase2 = secondTerm.split("[:]")[0];
		} else if (token.contains("/")) {
			varInfo = token.split("[//]");
			String secondTerm = varInfo[1];
			GT_phase2 = secondTerm;
		}
		GT_phase1 = varInfo[0];
		// System.out.println(GT_phase1+"\t"+GT_phase2);
		if (GT_phase1.equals(".") || GT_phase2.equals(".")) {
			snp = (byte) ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueSymbol();
		} else {
			int hap = (Integer.parseInt(GT_phase1) + Integer.parseInt(GT_phase2));
			snp = (byte) (hap<3?hap:(byte) ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueSymbol());
		}
		return snp;
	}
}	

