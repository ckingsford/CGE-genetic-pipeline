package edu.cmu.cs.lane.pipeline.dataanalyzer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jblas.DoubleMatrix;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.cs.lane.brokers.ClinicalDictionary;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDatasetList;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.datatypes.evaluation.AbstractEvaluationFunction;
import edu.cmu.cs.lane.datatypes.evaluation.EvaluationResult;
import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.model.CGEModelFeatureBean;
import edu.cmu.cs.lane.datatypes.prediction.CGEPrediction;
import edu.cmu.cs.lane.datatypes.prediction.CGEPredictionBean;
import edu.cmu.cs.lane.datatypes.shotgun.FeatureMatrix;
import edu.cmu.cs.lane.pipeline.dataanalyzer.util.MatrixMarketUtil;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.settings.OptionsClassification;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
//import org.json.JSONException;
//import org.json.JSONObject;

/**
 * 
 * ShotgunAnalyzer implements the sparse logistic regression and the lasso algorithms as an {@link AbstractAnalyzer} object.
 * These algorithms are based on the Shotgun package written in C++ that are called using JNI. 
 * (See more information on the Shotgun project <a href="http://www.select.cs.cmu.edu/projects/shotgun/">here</a>).
 * </p>
 * Please note that this analyzer calls native call in C++ and requires the location of the Shotgun library (provided) to 
 * be defined in the properties file. 
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 * 
 * @see AbstractAnalyzer 
 */
public class ShotgunAnalyzer extends AbstractAnalyzer {

	private String algorithm;
	private double lambda;
	private int maxIter;
	private double threshold;
	private int regPathLength;
	private static boolean ShotgunLibraryLoaded = false;

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	public String getName() {
		return "ShotgunAnalyzer";
	}

	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * Creates a Shotgun Analyzer with the specified properties file
	 */
	public AbstractAnalyzer create() {
		// System.out.println("== create ==");
		if (OptionsFactory.getOptions("classification") == null){
			System.out.println("WARNING: Classification module not defined in 'modules' - creating new predictions may fail due to missing parameters");
		}

		ShotgunAnalyzer sa = new ShotgunAnalyzer();
		return sa;
	}
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#initializeAnalyzers(edu.cmu.cs.lane.datatypes.dataset.SamplesLabels, edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData)
	 */
	@Override
	public ArrayList<AbstractAnalyzer> initializeAnalyzers(SamplesDatasetList samplesDatasetList) {
		ArrayList<AbstractAnalyzer> analyzerInstances = new ArrayList<AbstractAnalyzer>();

		for (String algorithm : ((OptionsClassification) OptionsFactory.getOptions("classification")).getAlgorithms()) {
			for (double lambda : ((OptionsClassification) OptionsFactory.getOptions("classification")).getLambdas()){
				for (double threshold: ((OptionsClassification) OptionsFactory.getOptions("classification")).getThresholds()){
					int regPathLength = 0;
					if (((OptionsClassification) OptionsFactory.getOptions("classification")).getRegressionPathLengths() != null){ //relevant only for lasso
						regPathLength = ((OptionsClassification) OptionsFactory.getOptions("classification")).getRegressionPathLength(0);//no point of having multiple options for this parameters
					}						
					ShotgunAnalyzer analyzer = new ShotgunAnalyzer();
					analyzer.algorithm = algorithm;
					analyzer.maxIter = ((OptionsClassification) OptionsFactory.getOptions("classification")).getMaxIterations();
					analyzer.lambda = lambda;
					analyzer.threshold = threshold;
					analyzer.regPathLength = regPathLength;
					analyzerInstances.add(analyzer);
				}
			}
		}
		return analyzerInstances;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * return null to keep default behavior 
	 */
	@Override
	public ArrayList<SamplesLabels> loadLabelsOverride(ArrayList<SamplesLabels> labels) {
		return null;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * return null to keep default behavior 
	 */
	@Override
	public ArrayList<SamplesGeneticData> loadDataOverride(ArrayList<SamplesGeneticData> data, ArrayList<AbstractDataFilter> filters) {
		return null;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * @param model
	 */
	private double[] predict(CGEModel model, SamplesGeneticData samples){
		// multiplying the genotype of the patient with the prediction vector
		double[] samplePredictions = new double[samples.getSamplesCount()];
		for (int f_i = 0; f_i < samples.getFeaturesCount(); f_i++){//iterating over all relevant features of the samples
			if (model.hasFeature(samples.getFeatureName(f_i))){ //checking if the model contains the feature 
				String featureId = samples.getFeatureName(f_i);
				for (int p_i =0; p_i < samples.getSamplesCount(); p_i++){ //iterative over all samples	
					samplePredictions[p_i] += model.get(featureId).get(0).val * samples.getData(p_i,f_i); //multiplying the model feature beta with sample feature value
				}
			}
		}
		return samplePredictions;
	}
	
	/**
	 * return maximum feature val
	 * @param model
	 */
	private double predictMax(CGEModel model){
		double sum = 0;

		for (int i=0; i<model.getFeatures().size(); i++){
			if (model.getFeatures().get(i).val>0)
				sum += 2*model.getFeatures().get(i).val;
		}
		return sum;
	}
	

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#saveAnalyzerSettings()
	 */
	private AnalysisDetails getAnalyzerSettings() {
		AnalysisDetails analysisDetails = new AnalysisDetails();
		analysisDetails.algorithmName = getName();
		JSONObject params = new JSONObject();
		try {
			params.put("algorithm", this.algorithm);
			params.put("lambda", this.lambda);
			params.put("threshold", this.threshold);
			params.put("regPathLength",this.regPathLength);
			params.put("max_iterations", this.maxIter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		analysisDetails.algParameters = params.toString();
		analysisDetails.additionalInfo = ""; 
		analysisDetails.backgroundSource = "1000 genomes"; //TODO: temp example
		analysisDetails.targetSource = "obesity"; //TODO: temp example
		analysisDetails.fileShortPostfix = this.algorithm+"_"+String.format("%.3f", lambda)+ "_"+String.format("%.3f", threshold);
		return analysisDetails;
	}
	
	/**
	 * run the algorithm
	 */
	private void runAlgorithmNew() {
		System.out.println("== runAlgorithm - New ==");
		long startTime = System.nanoTime();

		if (this.algorithm.equalsIgnoreCase("lasso")) {
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.println("Running algorithm: lasso(" + lambda
						+ ", " + regPathLength + ", " + threshold
						+ ", " + maxIter + ", " + ((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()
						+ ")");
			//the data was already loaded previously
			lasso(lambda, regPathLength, threshold,
					maxIter, (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose() ? 1 : 0));
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()) System.out.println("lasso completed.");
		}
		if (this.algorithm.equalsIgnoreCase("logreg")) {
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.println("Running algorithm: logreg(" + this.lambda
						+ ", " + threshold + ", " + maxIter + ", "
						+ ((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose() + ")");
			//the data was already loaded previously
			logreg(this.lambda, this.threshold, this.maxIter, (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose() ? 1 : 0));
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()) System.out.println("logreg completed.");
		}
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		double seconds = elapsedTime / 1.0E09;
		System.out.println("**\tJava\t" + this.algorithm + "\t" + seconds + "\t");
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public CGEModel analyze(SamplesDatasetList samplesDatasetList) {
		// Load native functions
		if (!ShotgunLibraryLoaded) {
			System.loadLibrary("shotgun");
			ShotgunLibraryLoaded = true;
		}

		return runAnalysisNew(samplesDatasetList);
	}

	
	/**
	 * runs the analysis 
	 * @param cvIndices 
	 * @return 
	 */
	private CGEModel runAnalysisNew(SamplesDatasetList samplesDatasetList) {
		//convert input to MM format
		FeatureMatrix X = MatrixMarketUtil.createFeatureMatrix(samplesDatasetList.getSamplesGeneticDataList());
		double[] Y = MatrixMarketUtil.createBinaryLabelsVector(samplesDatasetList.getSamplesLabelsList());
		
		//load data into JNI
		readMatrix(X.getFeatureMatrix(), X.getRowCount(), X.getColumnCount());
		readVector(Y);
		runAlgorithmNew();

		double[] JNImodel = getResults();
		CGEModel model = new CGEModel();
		for (int i = 0; i < JNImodel.length; ++i) {
			if (JNImodel[i] == 0.0) { //sparse - no point of storing zeros
				continue;
			}
			CGEModelFeatureBean feature = new CGEModelFeatureBean();
			feature.id = samplesDatasetList.get(0).getGeneticData().getFeatureName(i); //presumably the target and background have the same features
			feature.type = samplesDatasetList.get(0).getGeneticData().getExtendedFeatureInfo(i); 
			feature.val = JNImodel[i];
			feature.var = samplesDatasetList.get(0).getLabelData().getFeatureName(0); //in classification there is only one column
			model.add(feature);
		}
		
		clearMemory();
		System.gc();
		model.setDetails(getAnalyzerSettings());
		//System.out.println("DEBUG: Model size:"+ model.getModel().size());
		return model;
		
		//write X and Y to file to verify output against standalone program
/*		try {
			FileWriter fw = new FileWriter(new File("X.txt"));
			String output = "";
			fw.write("%%MatrixMarket matrix coordinate real general\n");
			fw.write(String.format("%d %d %d\n", X.getRowCount(), X.getColumnCount(), X.getFeatureMatrix().length));
			for (int i=0; i< X.getFeatureMatrix().length; i++) {
				output = String.format("%.0f %.0f %.3f\n", X.getFeatureMatrix()[i][0], X.getFeatureMatrix()[i][1], X.getFeatureMatrix()[i][2]);
				fw.write(output);
			}
			fw.close();
			fw = new FileWriter(new File("Y.txt"));
			fw.write("%%MatrixMarket matrix coordinate real general\n");
			fw.write(String.format("%d 1 %d\n", Y.length, Y.length));
			for (int i=0; i< Y.length; i++) {
				output = String.format("%d 1 %.3f\n", i+1, Y[i]);
				fw.write(output);
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	*/
	}


	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public ArrayList<CGEPrediction> applyModel(CGEModel model, SamplesDataset samplesDataset) {
		double[] results = predict(model, samplesDataset.getGeneticData());
		double theoreticalMax = predictMax(model);
		ArrayList<CGEPrediction> predictions = new ArrayList<CGEPrediction>(results.length);
		for (int i=0; i<results.length;i++){
			CGEPrediction prediction = new CGEPrediction();
			prediction.setSampleId(samplesDataset.getGeneticData().getSampleName(i));
			prediction.setModelId(Integer.toString(model.details.id));
			CGEPredictionBean predictionBean = new CGEPredictionBean();
			predictionBean.id = model.getFeatures().get(0).var; //there is no specific variable for classification analysis 
			predictionBean.value = Double.toString(results[i]/theoreticalMax);
			prediction.add(predictionBean);
			predictions.add(prediction);
		}
		return predictions;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * @param model
	 * @param labels
	 * @param samples
	 * @param cvIndices
	 */
	@Override
	public ArrayList<EvaluationResult> testModel(CGEModel model, SamplesDatasetList samplesDatasetList, ArrayList<AbstractEvaluationFunction>evaluateFunctions) {
		
		ArrayList<double[]> predictions = new ArrayList<double[]>(samplesDatasetList.size());

		for (int dataset = 0;dataset < samplesDatasetList.size(); dataset++){
				//extracting indices for the relevant features
				ArrayList<Integer> featuresIndicesInteger = new ArrayList<Integer>(); //just in case there are features in the model that are not available for this patient hence feautresIndices may be smaller than model size
				ArrayList<String> featureIds = new ArrayList<String>();
				for (int f_i = 0; f_i < samplesDatasetList.get(dataset).getGeneticData().getFeaturesCount(); f_i++){ //iterating over all features
					if (model.hasFeature(samplesDatasetList.get(dataset).getGeneticData().getFeatureName(f_i))){
						featuresIndicesInteger.add(f_i);
						featureIds.add(samplesDatasetList.get(dataset).getGeneticData().getFeatureName(f_i));
					}
				}
				int[] feautresIndices = new int[featuresIndicesInteger.size()]; //moving indices to int[]
				for (int i=0; i < feautresIndices.length; i++){
					feautresIndices[i] = featuresIndicesInteger.get(i);
				}
				SamplesGeneticData slicedData = new SamplesGeneticData (samplesDatasetList.get(dataset).getGeneticData().sliceByFeatures(feautresIndices));
				predictions.add(predict(model, slicedData)); //rows are patients
		}
		
		//Evaluate accuracy of the predictions
		double accuracy=0;
		int count = 0;
		for (int dataset = 0;dataset < predictions.size(); dataset++){
			count += predictions.get(dataset).length;
			for (int p = 0; p < predictions.get(dataset).length; p++){ //iterating over samples
				if ((predictions.get(dataset)[p] >= 0? samplesDatasetList.get(0).getLabelData().getData(0, 0):samplesDatasetList.get(1).getLabelData().getData(0, 0)) == samplesDatasetList.get(dataset).getLabelData().getData(p, 0)){ //assumption in classification analysis there is only column - and two datasets - target and background
					accuracy++;
				}
			}
		} 
			
		ArrayList<EvaluationResult> evaluationResults = new ArrayList<EvaluationResult>();
		EvaluationResult accuracyObj = new EvaluationResult(); //TODO: replace with the more general implementation
		accuracyObj.setEvaluteFunction("accuracy");
		accuracyObj.setResult((double) accuracy / count);
		evaluationResults.add(accuracyObj);
		return evaluationResults;
	}


	/**
	 * 
	 * The set of functions below are used by JNI to pass data to the Shotgun library
	 * 
	 */
	
	/**
	 * Clears the memory in the shotgun data structure
	 */
	private native void clearMemory();

	/**
	 * Passes in a matrix market of features to be stored in the shotgun data
	 * structure
	 * 
	 * @param x
	 *            matrix market containing the data
	 * @param num_row
	 *            number of rows in the matrix
	 * @param num_col
	 *            number of vectors in the matrix
	 */
	public native void readMatrix(double[][] x, int num_row, int num_col);

	/**
	 * Passes in the vector containing the labels
	 * 
	 * @param y
	 *            vector of labels
	 */
	public native void readVector(double[] y);

	private native void lasso(double lambda, int regpathlength,
			double threshold, int maxiter, int verbose);

	private native void logreg(double lambda, double threshold, int maxiter,
			int verbose);

	public native double[] getResults();


	


	/*
	private double[] permuteY(double[] y) {
		// System.out.println("== permuteY ==");
		ArrayList<Double> list = new ArrayList<Double>();
		for (int i = 0; i < y.length; i++) {
			list.add(y[i]);
		}
		Collections.shuffle(list);
		for (int i = 0; i < y.length; i++) {
			y[i] = list.get(i);
		}
		return y;
	}
*/

}
