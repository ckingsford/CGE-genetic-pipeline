/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.util.Vector;

/**
 * @author zinman
 *
 */
public class SampleFilterBean {
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
	 * @return the geneticVariants
	 */
	public Vector<Byte> getGeneticVariants() {
		return geneticVariants;
	}
	/**
	 * @param geneticVariants the geneticVariants to set
	 */
	public void setGeneticVariants(Vector<Byte> geneticVariants) {
		this.geneticVariants = geneticVariants;
	}
	String sampleId;
	Vector<Byte> geneticVariants;
}
