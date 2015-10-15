/**
 * 
 */
package edu.cmu.cs.lane.brokers.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import edu.cmu.cs.lane.pipeline.util.FactoryUtils;

/**
 * @author zinman
 *
 */
public class GeneticPreprocessorFactory {
	static private HashSet<String> preprocessorControllerClassesNames = new HashSet<String>();
	static private Hashtable<String,AbstractCGEgeneticPreprocessingContorller> preprocessorControllerClasses = new Hashtable<String,AbstractCGEgeneticPreprocessingContorller>();
	
	static public void initialize() {
		preprocessorControllerClassesNames.add(CGEgeneticImputeController.class.getCanonicalName()); //load externally
		preprocessorControllerClassesNames.add(CGESamplesFilterController.class.getCanonicalName()); //load externally
		for (String ppClassName : preprocessorControllerClassesNames){
			try {
				AbstractCGEgeneticPreprocessingContorller ppClass = FactoryUtils.<AbstractCGEgeneticPreprocessingContorller> classInstantiator(ppClassName);
				preprocessorControllerClasses.put(ppClass.getName(), ppClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static public AbstractCGEgeneticPreprocessingContorller getController(String name){
		return preprocessorControllerClasses.get(name);
	}
}
