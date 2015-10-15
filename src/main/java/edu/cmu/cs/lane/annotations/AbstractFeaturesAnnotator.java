/**
 * 
 */
package edu.cmu.cs.lane.annotations;

import java.util.ArrayList;

/**
 * 
 * A wrapper for various features annotators
 * @author zinman
 *
 */
public abstract class AbstractFeaturesAnnotator {

	abstract public String getName();
	abstract public ArrayList<SampleGeneticFeatureBean> annotate(ArrayList<SampleGeneticFeatureBean> featuresInput);
}
