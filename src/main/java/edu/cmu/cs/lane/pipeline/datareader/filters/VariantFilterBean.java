/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.util.Vector;

/**
 * @author zinman
 *
 */
public class VariantFilterBean {
	 String fullId;
	 String rsId;
	 String quality;
	 Vector<Byte> snpVector;
	/**
	 * @return the fullId
	 */
	public String getFullId() {
		return fullId;
	}
	/**
	 * @param fullId the fullId to set
	 */
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	/**
	 * @return the rsIs
	 */
	public String getRsId() {
		return rsId;
	}
	/**
	 * @param rsIs the rsIs to set
	 */
	public void setRsId(String rsId) {
		this.rsId = rsId;
	}
	/**
	 * @return the quality
	 */
	public String getQuality() {
		return quality;
	}
	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}
	/**
	 * @return the snps
	 */
	public Vector<Byte> getSnpVector() {
		return snpVector;
	}
	/**
	 * @param snps the snps to set
	 */
	public void setSnps(Vector<Byte> snps) {
		this.snpVector = snps;
	}
	
}
