package edu.cmu.cs.lane.datatypes.dataset;

import java.util.Hashtable;
import java.util.Set;

import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;


/**
 * The class SampleGeneticBean represents (genetic) information of a specific patient. The actual values are customarily 
 * represented as 0:no mutations, 1:mutation on one allele, 2:mutations on both alleles, 3:missing data
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 */

public class SampleGeneticBean {
	
	/**
	 * the ID of the sample.
	 */
	public String id;
	/**
	 * Array representing the identifiers of the features measured for this sample
	 */
	
	protected Hashtable<String, SampleGeneticFeatureBean> features;
	public SampleGeneticBean() {
		features =  new Hashtable<String, SampleGeneticFeatureBean>();
	}
	/**
	 * a constructor that instantiate a sample with certain number of feature elements.
	 */
	public SampleGeneticBean(int size) {
		features = new Hashtable<String, SampleGeneticFeatureBean>(size);
	}
	/**
	 * 
	 * @param id 			feature id
	 * @param info 			feature additional info
	 * @param value			feature value as a Byte - customarily represented as 0:no mutations, 1:mutation on one allele, 2:mutations on both alleles, 3:missing data
	 */
	public void addFeature(SampleGeneticFeatureBean feature){
		features.put(feature.id, feature);
	}
	/**
	 * @return the sampleId
	 */
	public String getSampleId() {
		return id;
	}
	/**
	 * @param sampleId the sampleId to set
	 */
	public void setSampleId(String sampleId) {
		this.id = sampleId;
	}
	/**
	 * @return the featureIds
	 */
	public Set<String> getFeatureIds() {
		return features.keySet();
	}
	/**
	 * @return the featureValues
	 */
	public SampleGeneticFeatureBean getFeature(String id) {
		return features.get(id);
	}
	
	
}
