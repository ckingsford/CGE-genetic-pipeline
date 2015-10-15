/**
 * 
 */
package edu.cmu.cs.lane.brokers.preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import edu.cmu.cs.lane.pipeline.util.FactoryUtils;

/**
 * A factory to create preprocessing clinical var
 * @author zinman
 *
 */
public class ClinicalPreprocessorFactory {
	static private HashSet<String> preprocessorControllerClassesNames = new HashSet<String>();
	static private Hashtable<String,AbstractCGEclinicalPreprocessingContorller> preprocessorControllerClasses = new Hashtable<String,AbstractCGEclinicalPreprocessingContorller>();
	
	static public void initialize() {
		preprocessorControllerClassesNames.add(CGEclinicalImputeController.class.getCanonicalName()); //load externally
		for (String ppClassName : preprocessorControllerClassesNames){
			try {
				AbstractCGEclinicalPreprocessingContorller ppClass = FactoryUtils.<AbstractCGEclinicalPreprocessingContorller> classInstantiator(ppClassName);
				preprocessorControllerClasses.put(ppClass.getName(), ppClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static public AbstractCGEclinicalPreprocessingContorller getController(String name){
		return preprocessorControllerClasses.get(name);
	}
}
