package edu.cmu.cs.lane.pipeline.dataanalyzer;

import java.util.ArrayList;
import java.util.Hashtable;

import com.mongodb.util.JSON;

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
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;

/**
 *  A simple example for code developers intended to show how the functionality of CGE can be extended by adding a new analyzer
 * 
 *  @author zinman
 *	@version 1.0
 *	@since 1.0
 */
public class NBAnalyzer extends AbstractAnalyzer {
	
	double laplacian = 1;
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public String getName() {
		return "NBAnalyzer";
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public AbstractAnalyzer create() {
		NBAnalyzer example = new NBAnalyzer();	
		return example;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * To be used for override default load behavior. Return null to keep default
	 */
	@Override
	public ArrayList<SamplesLabels> loadLabelsOverride(ArrayList<SamplesLabels> labels) {
		return null;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * To be used for override default load behavior. Return null to keep default
	 */
	@Override
	public ArrayList<SamplesGeneticData> loadDataOverride(ArrayList<SamplesGeneticData>  data, ArrayList<AbstractDataFilter> filters) {
		return null;
	}
	
	/**
	 * 
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#initializeAnalyzers(edu.cmu.cs.lane.datatypes.dataset.SamplesLabels, edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData)
	 */
	@Override
	public ArrayList<AbstractAnalyzer> initializeAnalyzers(SamplesDatasetList samplesDatasetList) {
		ArrayList<AbstractAnalyzer> NBanalyzers = new ArrayList<AbstractAnalyzer>();
		NBAnalyzer analyzer = new NBAnalyzer();
		analyzer.laplacian = 1; //Optional - get it from properties file
		NBanalyzers.add(analyzer);
		return NBanalyzers;
	}


	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public CGEModel analyze(SamplesDatasetList samplesDatasetList) {
		CGEModel model = new CGEModel();
		

		//calculating probabilities for each variant in each dataset
		ArrayList<ArrayList<int[]>> counts = new ArrayList<ArrayList<int[]>>(samplesDatasetList.size());	
		for (int d=0; d < samplesDatasetList.size(); d++){ //iterating over all datasets
			counts.add(new ArrayList<int[]>(samplesDatasetList.get(d).getGeneticData().getSamplesCount())); //adding variant probability vector for each dataset
			
			//calculating probability of variant v given dataset d
			for (int v=0; v < samplesDatasetList.get(d).getGeneticData().getFeaturesCount(); v++){
				counts.get(d).add(new int[3]); //assumption values are after imputation and labeled 0,1,2
				
				//iterating over all samples
				for (int s=0; s<samplesDatasetList.get(d).getGeneticData().getSamplesCount();s++){ 	
					counts.get(d).get(v)[(int)samplesDatasetList.get(d).getGeneticData().getData(s,v)]++;
				}
			}
		}
		
		//Calculating probabilities for variant classes for each dataset
		ArrayList<ArrayList<double[]>> probs = new ArrayList<ArrayList<double[]>>(samplesDatasetList.size());
		for (int d=0; d < samplesDatasetList.size(); d++){ //iterating over all datasets
			String datasetLabel = Integer.toString((int)samplesDatasetList.get(d).getLabelData().getData(0,0)); //labels for the entire dataset are identical so we can take only the first sample; In classification analysis there is only one class label (one column) 
			probs.add(new ArrayList<double[]>(samplesDatasetList.get(d).getGeneticData().getSamplesCount())); //adding variant probability vector for each dataset
			
			//calculating probability of variant v given dataset d
			for (int v=0; v < samplesDatasetList.get(d).getGeneticData().getFeaturesCount(); v++){
				probs.get(d).add(new double[3]); //corresponding to classes 0,1,2

				for (int c = 0; c<=2; c++){ //iterate over the 3 classes
					probs.get(d).get(v)[c] = ((double)counts.get(d).get(v)[c] + laplacian * 1)/(samplesDatasetList.get(d).getGeneticData().getSamplesCount() + laplacian * probs.get(d).get(0).length); 
					
					CGEModelFeatureBean feature = new CGEModelFeatureBean();
					feature.id = samplesDatasetList.get(d).getGeneticData().getFeatureName(v); //Setting the name of variant
					feature.type = samplesDatasetList.get(d).getGeneticData().getExtendedFeatureInfo(v); 
					feature.val = probs.get(d).get(v)[c];
					feature.var = datasetLabel + ":" + c; //the response variable is the datasetLabel and class
					model.add(feature);
				}
			}
		}
		
		AnalysisDetails details = new AnalysisDetails();
		details.additionalInfo = JSON.serialize(new Double(laplacian)); //Example: store as JSON - just an example how the model parameters can be stored 
		model.setDetails(details);

		return model;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * return the class label as prediction for each sample in the input
	 */
	@Override
	public ArrayList<CGEPrediction> applyModel(CGEModel model, SamplesDataset samplesDataset) {
		ArrayList<CGEPrediction> predictions = new ArrayList<CGEPrediction>(samplesDataset.getGeneticData().getSamplesCount());
		
		//iterating over all samples in the input
		for (int s=0; s<samplesDataset.getGeneticData().getSamplesCount(); s++){
			Hashtable<String, Double> sampleProb = new Hashtable<String,Double>(); 
			
			//iterating over all features of the sample
			for (int v=0; v< samplesDataset.getGeneticData().getFeaturesCount(); v++){
				if (model.hasFeature(samplesDataset.getGeneticData().getFeatureName(v))){
					ArrayList<CGEModelFeatureBean> featureElements = model.get(samplesDataset.getGeneticData().getFeatureName(v));
					for (int f=0; f<featureElements.size(); f++){
						String[] classInfoTokens = featureElements.get(f).var.split(":");
						int c = Integer.parseInt(classInfoTokens[1]);
						if (c == (int)samplesDataset.getGeneticData().getData(s,v)) {  //if the possible class of the feature element matches the inspected sample class
							String featureSampleLabel = classInfoTokens[0];
							if (!sampleProb.containsKey(featureSampleLabel)){
								sampleProb.put(featureSampleLabel, new Double(0));
							}
							Double val = sampleProb.get(featureSampleLabel) - Math.log(featureElements.get(f).val); //use log as probability values will become intractable; negating values as log of probs will be negative
							sampleProb.put(featureSampleLabel, val);  
							
						}
					}
				}
			}
			
			String predictedLabel = null;
			double prob = Double.MAX_VALUE;
			//setting the prediction value to be the argmin of class labels for this sample
			for (String label : sampleProb.keySet()){
				if (sampleProb.get(label) < prob){
					prob = sampleProb.get(label);
					predictedLabel = label;
				}
			}
			CGEPrediction prediction = new CGEPrediction();
			CGEPredictionBean predVal = new CGEPredictionBean();
			predVal.id = predictedLabel;
			predVal.value = Double.toString(prob); // potentially this should indicate the probability confidence in the prediction 
			ArrayList<CGEPredictionBean> predVals = new ArrayList<CGEPredictionBean>(1);
			prediction.setPredictions(predVals);
			predictions.add(prediction);
		}
		return predictions;
	}

	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#testModel(edu.cmu.cs.lane.datatypes.model.CGEModel, java.util.ArrayList, java.util.ArrayList, edu.cmu.cs.lane.datatypes.CVindices)
	 */
	@Override
	public ArrayList<EvaluationResult> testModel(CGEModel model, SamplesDatasetList samplesDatasetList, ArrayList<AbstractEvaluationFunction>evaluateFunctions) {
		int correctCount = 0;
		int totalPreds = 0;
		ArrayList<ArrayList<Hashtable<String, Double>>> sampleProb = new ArrayList<ArrayList<Hashtable<String,Double>>>(); 

		for (int d = 0; d< samplesDatasetList.size(); d++){
			totalPreds += samplesDatasetList.get(d).getGeneticData().getSamplesCount();
			sampleProb.add(new ArrayList<Hashtable<String,Double>>());
			for (int s=0; s<samplesDatasetList.get(d).getGeneticData().getSamplesCount(); s++){	
				sampleProb.get(d).add(new Hashtable<String, Double>());
				//iterating over all features of the sample
				for (int v=0; v< samplesDatasetList.get(d).getGeneticData().getFeaturesCount(); v++){
					if (model.hasFeature(samplesDatasetList.get(d).getGeneticData().getFeatureName(v))){
						ArrayList<CGEModelFeatureBean> featureElements = model.get(samplesDatasetList.get(d).getGeneticData().getFeatureName(v));
						for (int f=0; f<featureElements.size(); f++){
							String[] classInfoTokens = featureElements.get(f).var.split(":");
							int c = Integer.parseInt(classInfoTokens[1]);
							if (c == (int)samplesDatasetList.get(d).getGeneticData().getData(s,v)) { //if the possible class of the feature element matches the inspected sample class
								String featureSampleLabel = classInfoTokens[0];
								if (!sampleProb.get(d).get(s).containsKey(featureSampleLabel)){
									sampleProb.get(d).get(s).put(featureSampleLabel, new Double(0));
								}
								Double val = sampleProb.get(d).get(s).get(featureSampleLabel) - Math.log(featureElements.get(f).val); //use log as probability values will become intractable; negating values as log of probs will be negative
								sampleProb.get(d).get(s).put(featureSampleLabel, val);  	
							}
						}
					}
				}
			}
		}
		
		
		for (int d = 0; d< samplesDatasetList.size(); d++){
			for (int s=0; s<samplesDatasetList.get(d).getGeneticData().getSamplesCount(); s++){	
				String predictedLabel = null;
				double prob = Double.MAX_VALUE;
				//setting the prediction value to be the argmin of class labels for this sample
				for (String label : sampleProb.get(d).get(s).keySet()){
					if (sampleProb.get(d).get(s).get(label) < prob){
						prob = sampleProb.get(d).get(s).get(label);
						predictedLabel = label;
					}
				}
				
				if (Integer.parseInt(predictedLabel) == (int)samplesDatasetList.get(d).getLabelData().getData(0,0)){ //presumably all dataset has the same value as the first sample in the only column
					correctCount++;
				}
			}
		}
		
		ArrayList<EvaluationResult> evaluationResults = new ArrayList<EvaluationResult>();
		EvaluationResult accuracy = new EvaluationResult(); //TODO: replace with the more general implementation
		accuracy.setEvaluteFunction("accuracy");
		accuracy.setResult((double) correctCount / totalPreds);
		evaluationResults.add(accuracy);
		return evaluationResults;
	}

	
}
