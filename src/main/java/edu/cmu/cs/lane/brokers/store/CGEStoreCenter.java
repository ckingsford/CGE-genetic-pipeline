/**
 * 
 */
package edu.cmu.cs.lane.brokers.store;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * @author zinman
 *
 */
public class CGEStoreCenter {
	
	static Hashtable<String, String> moduleClasses = new Hashtable<String,String>();
	static Hashtable<String, AbstractStoreModule> hashModules = new Hashtable<String, AbstractStoreModule>();
	
	static public void initialize() {
		moduleClasses.put("mysql", MySQLStoreModule.class.getCanonicalName());//TODO: the 'store' modules need to be registered dynamically
		moduleClasses.put("file", FileStoreModule.class.getCanonicalName());
		
		String[] storeTargets = ((OptionsGeneral) OptionsFactory.getOptions("general")).getStoreTargets();
		if (storeTargets != null){
			for (String target: storeTargets){
				if(moduleClasses.containsKey(target.toLowerCase())){ 
					Class<?> c;
					try {
						c = Class.forName(moduleClasses.get(target.toLowerCase()));
						hashModules.put(target.toLowerCase(), (AbstractStoreModule) c.newInstance());
					} catch (ClassNotFoundException e) {
						System.out.println("ERROR: No class defined for StoreModule "+ target);
						e.printStackTrace();
					} catch (InstantiationException e) {
						System.out.println("ERROR: No class defined for StoreModule "+ target);
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						System.out.println("ERROR: No class defined for StoreModule "+ target);
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @param model
	 * @param analyzer
	 */
	public static int storeModel(CGEModel model) {
		int returnId = -1;
		int tempReturn;
		for (AbstractStoreModule storeModule:hashModules.values()){
			tempReturn = storeModule.storeModel(model);
			if (tempReturn >= 0) {
				returnId = tempReturn;
			}
		}
		return returnId;
	}

	/**
	 * @param analysesIds
	 */
	public static int storeSet(ArrayList<Integer> analysesIds, AnalysisSetDetails analysisSetDetails) {
		int returnId = -1;
		int tempReturn;
		for (AbstractStoreModule storeModule:hashModules.values()){
			tempReturn = storeModule.storeSet(analysesIds, analysisSetDetails);
			if (tempReturn >= 0) {
				returnId = tempReturn;
			}
		}
		return returnId;
	}
}
