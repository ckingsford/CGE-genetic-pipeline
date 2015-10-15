package edu.cmu.cs.lane.pipeline.dataanalyzer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jblas.DoubleMatrix;

import edu.cmu.cs.lane.brokers.load.CGELoadClinicalCenter;
import edu.cmu.cs.lane.brokers.load.CGELoadGeneticCenter;
import edu.cmu.cs.lane.brokers.store.CGEStoreCenter;
import edu.cmu.cs.lane.datatypes.dataset.BatchInfo;
import edu.cmu.cs.lane.datatypes.dataset.CVSamplesDatasetList;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDatasetList;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.datatypes.evaluation.AbstractEvaluationFunction;
import edu.cmu.cs.lane.datatypes.evaluation.EvaluationResult;
import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.pipeline.datareader.DataReaderPhaseClassFactory;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsInternal;

/**
 * The DataAnalysisPhaseController class controls the data analysis phase including loading and running all 
 * the analyzers defined in the options.
 * 
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 */
public class DataAnalysisPhaseController extends PhaseController {

	private static final Logger logger = Logger
			.getLogger(DataAnalysisPhaseController.class);

	/**
	 * Runs the analysis phase including creating, initializing, and running all the analyzers defined in the options
	 */
	@Override
	public void runPhase() {
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("Running data analysis phase");

		setJavaLibPath( ((OptionsInternal)OptionsFactory.getOptions("internal")).getNativeLibLoc());
		DataAnalyzersFactory.initialize();

		String[] analyzerNames = ((OptionsGeneral) OptionsFactory.getOptions("general")).getAnalyzers();
		
		//loading data filters
		String[] filters = ((OptionsGeneral) OptionsFactory.getOptions("general")).getFilters();
		ArrayList<AbstractDataFilter> dataFilters = null;
		if (filters!=null){
			DataReaderPhaseClassFactory<AbstractDataFilter> dataFilterFactory = new DataReaderPhaseClassFactory<AbstractDataFilter>();
			dataFilterFactory.initialize("filters");
			dataFilters = new ArrayList<AbstractDataFilter>();
			for (String filter : filters) {
				dataFilters.add(dataFilterFactory.create(filter));
			}
		}
		AbstractAnalyzer analyzerPrototype;
		for (String analyzerName : analyzerNames) {
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.println("\tStarting Analyzer: " + analyzerName);
			analyzerPrototype = DataAnalyzersFactory.create(analyzerName);
			
			ArrayList<Integer> analysesIds = new ArrayList<Integer>();
			
			SamplesDatasetList samplesDatasetList = new SamplesDatasetList();
			samplesDatasetList.setSamplesLabels(CGELoadClinicalCenter.loadLabels());
			samplesDatasetList.setSamplesLabels(analyzerPrototype.loadLabels(samplesDatasetList.getSamplesLabelsList()));	//calling the analyzer override in case the analyzer wishes to override the load data
					 
			boolean geneticInfoProvided = false;
			ArrayList<ArrayList<BatchInfo>> batchIds = CGELoadGeneticCenter.getBatchIDs();
			int batchesCount = batchIds.get(0).size();// we should always have at least one group
			//Possibility - this can be done on different threads - one for each batch processing
			for (int batch=0; batch<batchesCount; batch++) {
				ArrayList<BatchInfo> groupIds = new ArrayList<BatchInfo>(batchIds.size()); //note: number of groups rather number of batches
				for (int group =0; group < batchIds.size(); group++){
					groupIds.add(batchIds.get(group).get(batch));
				}
				samplesDatasetList.setSamplesGeneticDataList(CGELoadGeneticCenter.loadBatch(groupIds, dataFilters));
				samplesDatasetList.setSamplesGeneticDataList(analyzerPrototype.loadData(samplesDatasetList.getSamplesGeneticDataList(), dataFilters)); //calling the analyzer override in case the analyzer wishes to override the load data
								
				geneticInfoProvided = true;
				//If no data labels were provided we have to set background data labels automatically
				if ((samplesDatasetList.getSamplesLabelsList() == null || samplesDatasetList.getSamplesLabelsList().size() == 0 || samplesDatasetList.getSamplesLabelsList().get(0) == null) && ((OptionsGeneral) OptionsFactory.getOptions("general")).isUseBackground()){
					samplesDatasetList.setSamplesLabels(setLabelsForBackgroundData(samplesDatasetList.getSamplesGeneticDataList()));
				}

				ArrayList<AbstractAnalyzer> analyzers = analyzerPrototype.initializeAnalyzers(samplesDatasetList);
				for (int i=0; i<analyzers.size(); i++){ 				
					ArrayList<Double> avgEvaluations= null;
					ArrayList<String> evaluationMeasureNames = null;
					//performing parameter selection using train test data in case test-set size is bigger than 0
					if (((OptionsGeneral) OptionsFactory.getOptions("general")).getTestSetPercent() > 0){
						double testSetPercent = ((OptionsGeneral) OptionsFactory.getOptions("general")).getTestSetPercent();
						CVSamplesDatasetList CVsamplesDatasetList = getTrainTestDatasets(samplesDatasetList,testSetPercent); 
						CGEModel model = analyzers.get(i).analyze(CVsamplesDatasetList.getTrainingSet());
						ArrayList<AbstractEvaluationFunction> evaluationFunctions = new ArrayList<AbstractEvaluationFunction>();		
						ArrayList<EvaluationResult> evaluationResults = analyzers.get(i).testModel(model, CVsamplesDatasetList.getTestingSet(), evaluationFunctions); 
						if (avgEvaluations == null){
							avgEvaluations = new ArrayList<Double>(evaluationResults.size());
							for (int e=0; e<evaluationResults.size();e++){
								avgEvaluations.add(0.0);
							}
						}
						if (evaluationMeasureNames == null){  //initialized only once
							evaluationMeasureNames = new ArrayList<String>(evaluationResults.size());
							for (int e=0; e<evaluationResults.size();e++){
								evaluationMeasureNames.add(evaluationResults.get(e).getEvaluteFunction());
							}
						}
						for (int e=0; e<evaluationResults.size();e++){
							double evaluation = evaluationResults.get(e).getResult(); 
							avgEvaluations.set(e, avgEvaluations.get(e)+evaluation);
							if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()) {
								System.out.println("Evaluation measure " + evaluationResults.get(e).getEvaluteFunction() + " " + evaluation);
							}
						}	
					}
					
					//running cross validation in case it is defined in the properties file
					if (((OptionsGeneral) OptionsFactory.getOptions("general")).getCvPercent() > 0){
						double cvPercent = ((OptionsGeneral) OptionsFactory.getOptions("general")).getCvPercent();
						int CViterationCount = getCVbatchCount(cvPercent);
						for(int cvGroup = 0; cvGroup < CViterationCount; cvGroup++){
							CVSamplesDatasetList CVsamplesDatasetList = getCVdataset(samplesDatasetList,cvPercent,cvGroup); 
							CGEModel model = analyzers.get(i).analyze(CVsamplesDatasetList.getTrainingSet());
							ArrayList<AbstractEvaluationFunction> evaluationFunctions = new ArrayList<AbstractEvaluationFunction>();		
							ArrayList<EvaluationResult> evaluationResults = analyzers.get(i).testModel(model, CVsamplesDatasetList.getTestingSet(), evaluationFunctions); 
							if (avgEvaluations == null){
								avgEvaluations = new ArrayList<Double>(evaluationResults.size());
								for (int e=0; e<evaluationResults.size();e++){
									avgEvaluations.add(0.0);
								}
							}
							if (evaluationMeasureNames == null){  //initialized only once
								evaluationMeasureNames = new ArrayList<String>(evaluationResults.size());
								for (int e=0; e<evaluationResults.size();e++){
									evaluationMeasureNames.add(evaluationResults.get(e).getEvaluteFunction());
								}
							}
							for (int e=0; e<evaluationResults.size();e++){
								double evaluation = evaluationResults.get(e).getResult(); 
								avgEvaluations.set(e, avgEvaluations.get(e)+evaluation);
							}
						}
						for (int e=0; e<avgEvaluations.size();e++){
							avgEvaluations.set(e, avgEvaluations.get(e)/ getCVbatchCount(cvPercent));
						}
					}
					
					CGEModel model = analyzers.get(i).analyze(samplesDatasetList); //calculating the full model
					if (((OptionsGeneral) OptionsFactory.getOptions("general")).getCvPercent() > 0) {
						model.getDetails().cvPercent = avgEvaluations.get(0); //assuming the first evaluation is the most relevant one - alternatively change cvPercent to a String and store info for all evaluations
						if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()) {
							double cvPercent = ((OptionsGeneral) OptionsFactory.getOptions("general")).getCvPercent();
							int CViterationCount = getCVbatchCount(cvPercent);
							for (int e=0; e<avgEvaluations.size();e++){
								System.out.println("Average " + evaluationMeasureNames.get(e) + ": " + avgEvaluations.get(e) + " using " + CViterationCount + " folds CV");
							}
						}
					}
					int analysisId =CGEStoreCenter.storeModel(model);  //storing the full model without CV for each possible analyzer
					if (analysisId != -1){
						analysesIds.add(analysisId);
					}
				}
			}
			if (!geneticInfoProvided) {
				System.out.println("WARNING: Genetic information was not provided.");
			}else{
				//store analysesSet (e.g., for all files in the directory)
				AnalysisSetDetails analysisSetDetails = new AnalysisSetDetails();
				analysisSetDetails.name = ((OptionsGeneral) OptionsFactory.getOptions("general")).getAnalysisName();
				analysisSetDetails.additionalInfo = analyzerPrototype.getAnalysisPrototypeInfo(samplesDatasetList.getSamplesLabelsList());
				CGEStoreCenter.storeSet(analysesIds, analysisSetDetails);	
					
				if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
					System.out.println("\tFinished Analyzer: " + analyzerName);
			}
		}
	}
	
	/**
	 * @param samplesDatasetList
	 * @param cvPercent
	 * @param cvGroup
	 * @return a split of a dataset to train and test data
	 */
	private CVSamplesDatasetList getTrainTestDatasets(SamplesDatasetList samplesDatasetList, double testSetPercent) {
		SamplesDatasetList samplesDatasetListTraining = new SamplesDatasetList();		
		SamplesDatasetList samplesDatasetListTesting = new SamplesDatasetList();
		Random rand = new Random();
		for (int dataset = 0; dataset < samplesDatasetList.size(); dataset++){
			int samplesCount = samplesDatasetList.get(dataset).getGeneticData().getSamplesCount();
			ArrayList<Integer> trainSetIndicesArray = new ArrayList<Integer>();
			ArrayList<Integer> testSetIndicesArray = new ArrayList<Integer>();
			for (int i=0; i<samplesCount;i++){
				if (rand.nextDouble() < testSetPercent) {
					testSetIndicesArray.add(i);
				}else{
					trainSetIndicesArray.add(i);
				}
			}
			
			int[] trainingSet = new int[trainSetIndicesArray.size()];
			for (int i=0; i<trainSetIndicesArray.size(); i++){
				trainingSet[i] = trainSetIndicesArray.get(i);
			}
			int[] testingSet = new int[testSetIndicesArray.size()];
			for (int i=0; i<testSetIndicesArray.size(); i++){
				testingSet[i] = testSetIndicesArray.get(i);
			}
			
			SamplesDataset cvTrainingDataset = samplesDatasetList.get(dataset).sliceBySamples(trainingSet);
			samplesDatasetListTraining.addSamplesDataset(cvTrainingDataset);
			
			SamplesDataset cvTestingDataset = samplesDatasetList.get(dataset).sliceBySamples(testingSet);
			samplesDatasetListTesting.addSamplesDataset(cvTestingDataset);
		}
		CVSamplesDatasetList cvSets = new CVSamplesDatasetList();
		cvSets.addTrainingDataset(samplesDatasetListTraining);
		cvSets.addTestingDataset(samplesDatasetListTesting);
		
		return cvSets;
	}
	
	/**
	 * @param samplesDatasetList
	 * @param cvPercent
	 * @param cvGroup
	 * @return a method that returns a cross validation batch based on a predefined percent and requested group batch
	 */
	private CVSamplesDatasetList getCVdataset(SamplesDatasetList samplesDatasetList, double cvPercent, int cvGroup) {
		SamplesDatasetList samplesDatasetListTraining = new SamplesDatasetList();		
		SamplesDatasetList samplesDatasetListTesting = new SamplesDatasetList();
		
		for (int dataset = 0; dataset < samplesDatasetList.size(); dataset++){
			int samplesCount = samplesDatasetList.get(dataset).getGeneticData().getSamplesCount();
			int theoreticalBatchSize = (int)(samplesCount*cvPercent);
			int testStartIndex = cvGroup * theoreticalBatchSize;
			int testEndIndex = Math.min(testStartIndex+theoreticalBatchSize, samplesCount); 
			int batchSize = testEndIndex - testStartIndex;
			int[] testingSet = new int[batchSize];
			int[] trainingSet = new int[samplesCount - batchSize]; 			
			int trainingCounter = 0;
			int testingCounter = 0;
			for (int i = 0; i < samplesCount; i++) {
				if (i >= testStartIndex && i <testEndIndex){
					testingSet[testingCounter] = i;
					testingCounter ++;
				} else {
					trainingSet[trainingCounter] = i;
					trainingCounter ++;
				}
			}

			SamplesDataset cvTrainingDataset = samplesDatasetList.get(dataset).sliceBySamples(trainingSet);
			samplesDatasetListTraining.addSamplesDataset(cvTrainingDataset);
			
			SamplesDataset cvTestingDataset = samplesDatasetList.get(dataset).sliceBySamples(testingSet);
			samplesDatasetListTesting.addSamplesDataset(cvTestingDataset);
		}
		CVSamplesDatasetList cvSets = new CVSamplesDatasetList();
		cvSets.addTrainingDataset(samplesDatasetListTraining);
		cvSets.addTestingDataset(samplesDatasetListTesting);
		
		return cvSets;
	}

	private int getCVbatchCount(double cvPercent){
		 return (int) (1/cvPercent);
	}

	/**
	 * 
	 * @param samplesGeneticData
	 * @return datasets with assigned labels to background data in case no data labels were provided
	 */
	private ArrayList<SamplesLabels> setLabelsForBackgroundData(ArrayList<SamplesGeneticData> samplesGeneticData) {
		if (samplesGeneticData.size() != 2){
			System.out.println("ERROR: number of datasets provided for case-control analysis is different than 2");
			System.exit(1);
		}
		ArrayList<SamplesLabels> newLabelsList = new ArrayList<SamplesLabels>(samplesGeneticData.size());
		double[] newMockLabels = {1,-1};//setting target as 1, background as -1
		for (int dataset_i=0; dataset_i < samplesGeneticData.size(); dataset_i++){
			//setting rows as samples
			int samplesCount = samplesGeneticData.get(dataset_i).getSamplesCount();
			//setting labels title
			SamplesLabels samplesLabels = new SamplesLabels();
			String[] colLabels = new String[1]; //add one column differentiating between labels
			colLabels[0] ="hasCondition"; //quite arbitrary 
			samplesLabels.initialize(samplesGeneticData.get(dataset_i).getSamplesNames(), colLabels);			

			for(int i=0; i<samplesCount; i++){//setting labels data 
				samplesLabels.setData(i,0,newMockLabels[dataset_i]);
			}
			newLabelsList.add(samplesLabels);
		}			
		
		return newLabelsList;
	}
	
	private void setJavaLibPath(String path) {
		System.setProperty("java.library.path", path);

		Field fieldSysPath;
		try {
			fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
