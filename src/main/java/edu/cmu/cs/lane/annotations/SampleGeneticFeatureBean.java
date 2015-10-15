/**
 * 
 */
package edu.cmu.cs.lane.annotations;

import java.util.ArrayList;



/**
 * 
 * A data bean that represents a genetic feature
 * @author zinman
 *
 */

public class SampleGeneticFeatureBean {
	public String id;
	public String rsId;
	public Byte value;
	public Double weight;
	public String extendedInfo; //e.g., the specific substitution info
	ArrayList<AbstractFeatureAnnotation> variantAnnotations; //presumably should be only one, although can be embedded
	/**
	 * @return the weight
	 */
	public Double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	/**
	 * @return the extendedInfo
	 */
	public String getExtendedInfo() {
		return extendedInfo;
	}
	/**
	 * @param extendedInfo the extendedInfo to set
	 */
	public void setExtendedInfo(String extendedInfo) {
		this.extendedInfo = extendedInfo;
	}
	/**
	 * @return the variantAnnotations
	 */
	public ArrayList<AbstractFeatureAnnotation> getVariantAnnotations() {
		return variantAnnotations;
	}
	/**
	 * @param variantAnnotations the variantAnnotations to set
	 */
	public void setVariantAnnotations(
			ArrayList<AbstractFeatureAnnotation> variantAnnotations) {
		this.variantAnnotations = variantAnnotations;
	}
	/**
	 * @return the effectAnnotations
	 */
	public ArrayList<AbstractFeatureAnnotation> getEffectAnnotations() {
		return effectAnnotations;
	}
	/**
	 * @param effectAnnotations the effectAnnotations to set
	 */
	public void setEffectAnnotations(
			ArrayList<AbstractFeatureAnnotation> effectAnnotations) {
		this.effectAnnotations = effectAnnotations;
	}
	ArrayList<AbstractFeatureAnnotation> effectAnnotations; //can be multiple for each effected transcript
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the rsId
	 */
	public String getRsId() {
		return rsId;
	}
	/**
	 * @param rsId the rsId to set
	 */
	public void setRsId(String rsId) {
		this.rsId = rsId;
	}
	/**
	 * @return the value
	 */
	public Byte getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Byte value) {
		this.value = value;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = id +" " + rsId + " " + value+ " " + weight;
		if (variantAnnotations!=null){
			for (int i=0; i<variantAnnotations.size();i++) {
				s+=" " +variantAnnotations.get(i).toString();
			}
		}
		if (effectAnnotations!=null){
			for (int i=0; i<effectAnnotations.size();i++) {
				s+=" " +effectAnnotations.get(i).toString();
			}
		}
		return s;
	}
	
	public void addVariantAnnotation(AbstractFeatureAnnotation annotation){
		if (variantAnnotations == null){
			variantAnnotations = new ArrayList<AbstractFeatureAnnotation>();
		}

		this.variantAnnotations.add(annotation);
	}
	
	public void addEffectAnnotation(AbstractFeatureAnnotation annotation){
		if (effectAnnotations == null){
			effectAnnotations = new ArrayList<AbstractFeatureAnnotation>();
		}

		this.effectAnnotations.add(annotation);
	}

}
