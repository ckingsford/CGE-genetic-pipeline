/**
 * 
 */
package edu.cmu.cs.lane.annotations;

import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * 
 * An annotation for SNPEFF annotation dataset. Provides info from SnpEff on transcript, effect, 
 * impact, functional class, coding, codon change, amino acid change, and exon. See SnpEff for more details.
 * @author zinman
 *
 */

//NOTE: add class to @XmlSeeAlso in AbstractFeatureAnnotation
public class SnpEffEffectAnnotation extends AbstractFeatureAnnotation {
	public String transcript;
	public String effect;
	public String impact;
	public String functionalClass;
	public String coding;
	public String codonChange;
	public String aminoAcidChange;
	public int exon;
	
	/**
	 * @see edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean#toString()
	 */
	@Override
	public String toString() {
		return  transcript + " "
				+ effect + " "
				+ impact + " "
				+ functionalClass + " "
				+ coding + " "
				+ codonChange + " "
				+ exon + " "
				+ aminoAcidChange ;	
	}
}
