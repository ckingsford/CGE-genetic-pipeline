package edu.cmu.cs.lane.datatypes.prediction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;

/**
 * The CGEPrediction class represents predictions collection that was done for a specific sample (represented by a unique sampleId)
 * using a specific model (represented by a model id)
 * @author zinman
 * @version 1.0
 * @since 1.0
 * 
 */
@XmlRootElement
public class CGEPrediction {
	/**
	 * The sample id for which this prediction refer to
	 */
	protected String sampleId;
	/**
	 * The model id that was used to generate the prediction
	 */
	protected String modelId;
	
	/**
	 * The list of predictions for each variable
	 */
	protected ArrayList<CGEPredictionBean> predictions;
	
	/**
	 * relevant features
	 */
	ArrayList<SampleGeneticFeatureBean> features;
	
	/**
	 * @return the sampleId
	 */
	public String getSampleId() {
		return sampleId;
	}

	/**
	 * @param sampleId the sampleId to set
	 */
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	/**
	 * @return the modelId
	 */
	public String getModelId() {
		return modelId;
	}

	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	/**
	 * @return the predictions
	 */
	public ArrayList<CGEPredictionBean> getPredictions() {
		return predictions;
	}

	/**
	 * @param predictions the predictions to set
	 */
	public void setPredictions(ArrayList<CGEPredictionBean> predictions) {
		this.predictions = predictions;
	}

	/**
	 * @return the features
	 */
	public ArrayList<SampleGeneticFeatureBean> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(ArrayList<SampleGeneticFeatureBean> features) {
		this.features = features;
	}

	/**
	 * default constructor. Instantiate the prediction array
	 */
	public CGEPrediction() {
		predictions = new ArrayList<CGEPredictionBean>();
	}
	
	/**
	 * constructor that instantiates a prediction list of a certain size
	 * @param size
	 */
	public CGEPrediction(int size) {
		predictions = new ArrayList<CGEPredictionBean>(size);
	}
	
	/**
	 * Adds prediction to the list
	 * @param predictionBean
	 */
	public void add(CGEPredictionBean predictionBean){
		if (predictions == null) predictions = new ArrayList<CGEPredictionBean>();
		predictions.add(predictionBean);
	}
	
	
	/**
	 * returns a set of the prediction ids for this sample / model
	 * @return a set of prediction element ids
	 */
	public ArrayList<CGEPredictionBean> getPredctions() {
		return predictions;
	}
}
