package edu.cmu.cs.lane.datatypes.dataset;

import java.util.Hashtable;
import java.util.Set;


/**
 * The class SampleLabelBean represents for each sample a hash of clinical variables or sample labels (.e.g., disease state). 
 * Each label is represented as a String-pair containing variable id and information related to the variable (possibly as a JSON object if needed) 

 *  
 * @author zinman
 * @version 1.0
 * @since 1.0
 */

public class SampleLabelBean {
	
	/**
	 * the ID of the sample.
	 */
	public String sampleId;
	/**
	 * possible information variables available for each sample
	 */
	public Hashtable<String,String> sampleVariables;

	
	/**
	 * default constructor. 
	 */
	public SampleLabelBean() {
		sampleVariables = new Hashtable<String, String>();
	}
	
	/**
	 * Adds a new variable for this sample
	 * @param variableId 		represented the id of the variable 
	 * @param value				represents the value of the variable
	 */
	public void addVariable(String variableId, String value){
		sampleVariables.put(variableId, value);
	}
	
	/**
	 * Returns the value of a certain value id
	 * @param variableId
	 * @return		value
	 */
	public String getVariable(String variableId){
		return sampleVariables.get(variableId);
	}
	
	/**
	 * get all the variable ids associated with a specific sample
	 * @return		variable ids
	 */
	public Set<String> getVariableIds(){
		return sampleVariables.keySet();
	}
	
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
	
}
