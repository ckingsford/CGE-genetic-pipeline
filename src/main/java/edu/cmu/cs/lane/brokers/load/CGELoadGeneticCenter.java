/**
 * 
 */
package edu.cmu.cs.lane.brokers.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.cmu.cs.lane.brokers.preprocessing.AbstractCGEgeneticPreprocessingContorller;
import edu.cmu.cs.lane.brokers.preprocessing.CGEgeneticImputeController;
import edu.cmu.cs.lane.brokers.preprocessing.CGESamplesFilterController;
import edu.cmu.cs.lane.brokers.preprocessing.GeneticPreprocessorFactory;
import edu.cmu.cs.lane.brokers.store.CGEStoreCenter;
import edu.cmu.cs.lane.brokers.store.CGEStoreGeneticDataCenter;
import edu.cmu.cs.lane.datatypes.dataset.BatchInfo;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.SimpleTableReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.VCFReader;
import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;
import edu.cmu.cs.lane.settings.OptionsPreprocessing;

/**
 * A central place to load genetic modules
 * @author zinman
 *
 */
public class CGELoadGeneticCenter {
	static private int iteratorLocation = -1;
	static private int iteratorMax;
	static private String source = "";
	static private AbstractLoadGeneticModule loadModule = null;
	static private Hashtable<String, String> moduleClasses = new Hashtable<String,String>();
	static private ArrayList<String> readerClasses = new ArrayList<String>();
	
	/**
	 * initialize the center with different modules
	 */
	static public void initialize() {
		moduleClasses.put("file", FileLoadGeneticModule.class.getCanonicalName());//TODO: better to 'load' modules need to be registered dynamically
		readerClasses.add(VCFReader.class.getCanonicalName());
		readerClasses.add(SimpleTableReader.class.getCanonicalName());
		
		source = ((OptionsGeneral) OptionsFactory.getOptions("general")).getLoadGeneticSource();	
		if (source != null){
			if(moduleClasses.containsKey(source.toLowerCase())){ 
				Class<?> c;
				try {
					c = Class.forName(moduleClasses.get(source.toLowerCase()));
					loadModule = (AbstractLoadGeneticModule) c.newInstance();
					loadModule.initialize(); 
					iteratorLocation = 0;
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR: No class defined for LoadGeneticModule "+ source);
					e.printStackTrace();
				} catch (InstantiationException e) {
					System.out.println("ERROR: No class defined for LoadGeneticModule "+ source);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("ERROR: No class defined for LoadGeneticModule "+ source);
					e.printStackTrace();
				}
				//Instantiating the reader classes
				for (int i=0; i<readerClasses.size();i++){
					try {
						Class.forName(readerClasses.get(i)).newInstance(); //the constructor of each class will register itself
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}			
		}else{
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()){
				System.out.println("WARNING: genetic data source not defined");
			}
		}
	}
	
	
	/**
	 * Advances the iterator 
	 * @param filters
	 * @return
	 */
	static public ArrayList<SamplesGeneticData> loadNext(ArrayList<AbstractDataFilter> filters){
		if (source != null){
			ArrayList<SamplesGeneticData> data = null;
			if (iteratorLocation < iteratorMax)	{
				data = loadModule.loadData(iteratorLocation, filters);
				iteratorLocation++;
				return data;	
			}	else {
				iteratorLocation = 0;
				return null;
			}
			
		}
		return null;
	}
	
	/**
	 * for a single load
	 * @param filters
	 * @return
	 */
	static public ArrayList<SamplesGeneticData> load(ArrayList<AbstractDataFilter> filters){
		if (source != null){
			return loadModule.loadData(iteratorLocation, filters);		
		}
		return null;
	}

	/**
	 * get a batch of IDs
	 * @return
	 */
	public static ArrayList<ArrayList<BatchInfo>> getBatchIDs() {
		if (source != null){
			return loadModule.getBatchIDs();		
		}
		return null;
	}
	
	/**
	 * load a batch of IDs 
	 * @param filters in case any data filters need to be applied
	 * @return
	 */
	static public ArrayList<SamplesGeneticData> loadBatch(ArrayList<BatchInfo> groupIds, ArrayList<AbstractDataFilter> filters){
		if (source != null){
			ArrayList<SamplesGeneticData> data = null;		

			List<AbstractCGEgeneticPreprocessingContorller> preProcessingControllers = new LinkedList<AbstractCGEgeneticPreprocessingContorller>();
			boolean useCheckpoints = ((OptionsLoadGenetic) OptionsFactory.getOptions("loadGenetic")).isStoreCheckpoints();
			String title = ((OptionsGeneral) OptionsFactory.getOptions("general")).getAnalysisName()+".target."+groupIds.get(0).title;

			
			String[] steps = ((OptionsPreprocessing) OptionsFactory.getOptions("preprocessing")).getSteps();
			if (steps == null){ //to support general loading without processing
				data = loadModule.loadData(groupIds, filters);
				if (((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground())
					data = syncDatasets(data);
			} else { //common when part of the analysis
				ArrayList<String> stepsList = new ArrayList<String>(steps.length);
				for (String step:steps){
					stepsList.add(step);
					preProcessingControllers.add(GeneticPreprocessorFactory.getController(step));
				}
	
				boolean preProcessingApplied = false;
				if (useCheckpoints){
					for (AbstractCGEgeneticPreprocessingContorller controller : preProcessingControllers) {
						preProcessingApplied &= controller.wasApplied(title);
					}
				}
				if (!useCheckpoints && !preProcessingApplied){ //load the input genetic data
					data = loadModule.loadData(groupIds, filters);
					if (((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground())
						data = syncDatasets(data);
				}
	
				//TODO: for generality better to deal with background files
				for (AbstractCGEgeneticPreprocessingContorller controller : preProcessingControllers) {
					data = controller.apply(data, useCheckpoints, title);
				}
			}
			
			return data;
			
		}
		return null;
	}
	
	/**
	 * Sync datasets to have the same set of variants
	 * @param datasets
	 * @return
	 */
	static public ArrayList<SamplesGeneticData> syncDatasets(ArrayList<SamplesGeneticData> datasets) {
		ArrayList<String[]> features = new ArrayList<String[]>(datasets.size());
		for (int i=0; i<datasets.size(); i++){
			features.add(datasets.get(i).getFeatureNames());
		}
		
		//Get the intersection between all datasets
		Set<String> s1 = new HashSet<String>(Arrays.asList(features.get(0)));
		for (int i=1; i<features.size(); i++){
			s1.retainAll(new HashSet<String>(Arrays.asList(features.get(i))));
		}
		//recreate datasets
		for (int i=0;i<features.size(); i++){
			int[] cindices = new int[s1.size()];
			int curIndex=0;
			for (int j=0; j<features.get(i).length;j++){
				if (s1.contains(features.get(i)[j])){
					cindices[curIndex] = j;
					curIndex ++;
				}
			}
			datasets.set(i,new SamplesGeneticData(datasets.get(i).sliceByFeatures(cindices)));
		}
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()) {System.out.println("Synced feature set contains "+s1.size() + " features");}
		return datasets;
	}
}
