/**
 * 
 */
package edu.cmu.cs.lane.annotations;

import java.util.ArrayList;
import java.util.HashMap;

import edu.cmu.cs.lane.pipeline.util.FactoryUtils;

/**
 * 
 * Serves as a factory to create new feature annotators
 * @author zinman
 *
 */
public class AnnotatorsFactory {
	static private HashMap<String, AbstractFeaturesAnnotator> factoryHash = new HashMap<String,AbstractFeaturesAnnotator>();

	static private ArrayList<String> annotatorClasses = new ArrayList<String>();
	
	/**
	 * initalize the factory
	 * @param additionalAnnotators
	 */
	static public void initialize(ArrayList<String> additionalAnnotators) { 
		annotatorClasses.add(SnpEffFeaturesAnnotator.class.getCanonicalName());
		for (String annotatorClass : annotatorClasses){
			AbstractFeaturesAnnotator newAnnotator;
			try {
				newAnnotator = FactoryUtils.classInstantiator(annotatorClass);
				factoryHash.put(newAnnotator.getName().toLowerCase(), newAnnotator);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (additionalAnnotators != null){
			for (String annotatorClass : additionalAnnotators){
				AbstractFeaturesAnnotator newAnnotator;
				try {
					newAnnotator = FactoryUtils.classInstantiator(annotatorClass);
					factoryHash.put(newAnnotator.getName().toLowerCase(), newAnnotator);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * create annotaor by name
	 * @param name
	 * @return
	 */
	static public AbstractFeaturesAnnotator getAnnotator(String name) {
		return factoryHash.get(name.toLowerCase());
	}
}
