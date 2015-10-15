/**
 * 
 */
package edu.cmu.cs.lane.brokers.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jblas.DoubleMatrix;
import org.jblas.ranges.RangeUtils;

import edu.cmu.cs.lane.datatypes.dataset.BatchInfo;
import edu.cmu.cs.lane.datatypes.dataset.MatrixLabels;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.matrixmath.MatrixUtils;
import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.AbstractGeneticFileDataReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.SimpleTableReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.VCFReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;

/**
 * @author zinman
 *
 */
public class FileLoadGeneticModule extends AbstractLoadGeneticModule {
	
	Hashtable<String,File> files;
	ArrayList<String> titles;
	Hashtable<String,File> backgroundFiles = null;
	ArrayList<String> backgroundTitles = null;
	
	/**
	 * @see edu.cmu.cs.lane.brokers.load.AbstractLoadGeneticModule#getName()
	 */
	@Override
	public String getName() {
		return "File";
	}
	
	/**
	 * @see edu.cmu.cs.lane.brokers.load.AbstractLoadGeneticModule#initialize()
	 */
	@Override
	public boolean initialize() {
		return true;
	}
	
	/**
	 * @see edu.cmu.cs.lane.brokers.load.AbstractLoadGeneticModule#initialize()
	 */
	@Override
	public ArrayList<ArrayList<BatchInfo>> getBatchIDs(){ //ids for each group (e.g., target / background)
		ArrayList<ArrayList<BatchInfo>> outIds = new ArrayList<ArrayList<BatchInfo>>();
		files = new Hashtable<String,File>();
		titles = new ArrayList<String>();
		outIds.add(new ArrayList<BatchInfo>());

		
		//String mainFolder = 		
		String targetFolder = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFolder();
	
		//load target folder
		File inTargetFolder = new File(targetFolder);
		if (!inTargetFolder.exists()) {
			System.err.println("Genentic info - Folder not found: " + targetFolder);
			System.exit(1);
		}
		
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground()){
			//load background folder if applicable
			String backgroundFolder = ((OptionsGeneral) OptionsFactory.getOptions("general")).getBackgroundInputFolder();
			File inBackgroundFolder = new File(backgroundFolder);
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground()){	
				backgroundFiles = new Hashtable<String,File>();
				backgroundTitles = new ArrayList<String>();
				outIds.add(new ArrayList<BatchInfo>());
				if (!inBackgroundFolder.exists()) {
					System.err.println("Genentic info - Background folder not found: " + backgroundFolder);
					System.exit(1);
				}
			}
		}
		
		//load target files
		File[] inTargetFiles = inTargetFolder.listFiles();
		Pattern inFilePattern = Pattern.compile(((OptionsLoadGenetic) OptionsFactory.getOptions("loadGenetic")).getGeneticInputFilePattern());
		Matcher matcher;
		String chrNum;
		boolean chrFound = false;

		for (File inTarget : inTargetFiles) {
			//data.inTarget = inTarget;
			matcher = inFilePattern.matcher(inTarget.getName());
			String title = inTarget.toString();
			if (matcher.find()) {
				if (matcher.groupCount()>0){
					chrNum = matcher.group(1);
					chrFound = true;
					title =chrNum; 
				}
				titles.add(title);
				files.put(title, inTarget);
				BatchInfo batchInfo = new BatchInfo();
				batchInfo.title = title;
				try {
					batchInfo.sourceInfo = inTarget.getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
				outIds.get(0).add(batchInfo);
			}	
		}
		Collections.sort(titles);
		
		//load background files if applicable
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground()){
			String backgroundFolder = ((OptionsGeneral) OptionsFactory.getOptions("general")).getBackgroundInputFolder();
			File inBackgroundFolder = new File(backgroundFolder);

			File[] inBGFiles = inBackgroundFolder.listFiles();
			Pattern inBGFilePattern = Pattern.compile(((OptionsLoadGenetic) OptionsFactory.getOptions("loadGenetic")).getBackgroundInputFilePattern());
			boolean BGchrFound = false;
			String BGtitle;
			for (File inBG : inBGFiles) {
				//data.inTarget = inTarget;
				matcher = inBGFilePattern.matcher(inBG.getName());
				BGtitle = inBG.toString();
				if (matcher.find()) {
					if (matcher.groupCount()>0){
						chrNum = matcher.group(1);
						BGtitle = chrNum;
						BGchrFound = true;
					}
					backgroundTitles.add(BGtitle);
					backgroundFiles.put(BGtitle,inBG);
					BatchInfo batchInfo = new BatchInfo();
					batchInfo.title = BGtitle;
					try {
						batchInfo.sourceInfo = inBG.getCanonicalPath();
					} catch (IOException e) {
						e.printStackTrace();
					}
					outIds.get(1).add(batchInfo);
				}	
			}
			if (titles.size() != backgroundTitles.size()){
				System.out.println("The number of background files do not match the number of target files");
				System.out.println("Target files: (" + targetFolder+ ")");
				for (int i=0; i< titles.size(); i++){
					System.out.println(titles.get(i));
				}
				System.out.println("Background files: (" + backgroundFolder+ ")");
				for (int i=0; i< backgroundTitles.size(); i++){
					System.out.println(backgroundTitles.get(i));
				}
				System.exit(1);
			}
			//verify that the titles are the same or quit
			if (chrFound && BGchrFound){
				Collections.sort(titles);
				Collections.sort(backgroundTitles);
				for (int i=0; i< titles.size(); i++){
					if (!titles.get(i).equalsIgnoreCase(backgroundTitles.get(i))){
						System.out.println("WARNING: background naming doesn't match target naming. Target: "+ titles.get(i)+ " Background: "+backgroundTitles.get(i));
					}
				}
			}
		}
		
		return outIds;	

	}
	
	private AbstractGeneticFileDataReader getReader(String filename){
		return ((OptionsLoadGenetic) OptionsFactory.getOptions("loadGenetic")).getReaderByFilename(filename);
	}

	/**
	 * @see edu.cmu.cs.lane.brokers.load.AbstractLoadGeneticModule#loadData(java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public ArrayList<SamplesGeneticData> loadData(ArrayList<BatchInfo> IDs, ArrayList<AbstractDataFilter> filters) {
		ArrayList<SamplesGeneticData> dataList = new ArrayList<SamplesGeneticData>();

		for (int dataset = 0; dataset < IDs.size(); dataset ++){
			String filename = IDs.get(dataset).sourceInfo;
			
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.println("\tLoading X: " + filename);
			
			AbstractGeneticFileDataReader reader = getReader(filename);	
			dataList.add(reader.read(filename, filters));
			
	
			if (dataList.get(dataset).getSamplesCount() == 0){
				System.out.println("ERROR: Genetic data encountered 0 samples");
			}
			if (dataList.get(dataset).getFeaturesCount() == 0){
				System.out.println("ERROR: Genetic data encountered 0 features");
			}
			
			
			if (reader.hasOrientationOptions() &&
					!((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputOrientation().equalsIgnoreCase("rows-as-patients")) {
				if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
					System.out.println("\tColumns as Patients selected. Transposing matrices.");
				dataList.get(dataset).transpose();
			} 
			
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.println("\tgenetic data stats: (samples) " + dataList.get(dataset).getSamplesCount() + " x (features) "+ dataList.get(dataset).getFeaturesCount());

		}
		return dataList;
	}
	
	
	//
	@Override
	public ArrayList<SamplesGeneticData> loadData(int index, ArrayList<AbstractDataFilter> filters) {
		
		ArrayList<ArrayList<BatchInfo>> outIds = new ArrayList<ArrayList<BatchInfo>>();
		files = new Hashtable<String,File>();
		titles = new ArrayList<String>();
		outIds.add(new ArrayList<BatchInfo>());

		
		String mainFolder = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFolder();		
		//String targetFolder = mainFolder + File.separator + "target";
		//String backgroundFolder = mainFolder + File.separator + "background";
	
		//load target folder
		File inTargetFolder = new File(mainFolder);
		if (!inTargetFolder.exists()) {
			System.err.println("Genentic info - Folder not found: " + mainFolder);
			System.exit(1);
		}

		
		
		ArrayList<BatchInfo> ids = new ArrayList<BatchInfo>();
		BatchInfo batchInfo = new BatchInfo();
		if (titles != null){
			batchInfo.title = titles.get(index);
		} else {
			batchInfo.title = files.get(index).getName();
		}
		try {
			batchInfo.sourceInfo = files.get(index).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ids.add(batchInfo);
		if (backgroundTitles == null){
			BatchInfo BGbatchInfo = new BatchInfo();
			BGbatchInfo.title = backgroundTitles.get(index);
			try {
				BGbatchInfo.sourceInfo = backgroundFiles.get(index).getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ids.add(BGbatchInfo);
		}
		return loadData(ids, filters);
	}


	
}
