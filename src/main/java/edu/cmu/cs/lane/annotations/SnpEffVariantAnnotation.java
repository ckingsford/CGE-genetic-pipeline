/**
 * 
 */
package edu.cmu.cs.lane.annotations;


/**
 * 
 * Variant information wrapper
 * @author zinman
 *
 */
//NOTE: add class to @XmlSeeAlso in AbstractFeatureAnnotation
public class SnpEffVariantAnnotation extends AbstractFeatureAnnotation {
	public String geneName;
	public String rsId;
	public String chr;
	public String pos;
	
	/**
	 * @see edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean#toString()
	 */
	@Override
	public String toString() {
		return geneName + " " 
				+ rsId + " "
				+ chr + " "
				+ pos;
	}
}
