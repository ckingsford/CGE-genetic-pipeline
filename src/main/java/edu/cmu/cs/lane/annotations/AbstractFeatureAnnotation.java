/**
 * 
 */
package edu.cmu.cs.lane.annotations;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A wrapper for annotations
 * @author zinman
 *
 */
@XmlSeeAlso({SnpEffVariantAnnotation.class, SnpEffEffectAnnotation.class}) //TODO: better to the marshaling dynamically or other alternative: http://stackoverflow.com/questions/16935649/alternative-to-xmlseealso
public class AbstractFeatureAnnotation {

}
