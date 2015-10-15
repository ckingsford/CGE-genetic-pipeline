package edu.cmu.cs.lane.datatypes.model;

/**
 * 
 * CGEModelFeatureBean represents a result (coefficient) of a model calculation on a specific feature for a specific response variable. 
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 * 
 */
public class CGEModelFeatureBean {
	/**
	 * contains the ID of the feature.
	 */
	public String id;
	/**
	 * contains the type of the feature (usually taken from Clinical Dictionary)
	 */
	public String type; 
	/**
	 * contains the ID of the response variable (disease state or clinical variable) that the feature result refers to.
	 */
	public String var;		
	/**
	 * contains the model-calculated value for the feature.
	 */
	public double val;
	
	@Override public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(id+"\t");
		sb.append(type + "\t");
		sb.append(var + "\t");
		sb.append(val + "\t");
		return sb.toString();
	}
}
