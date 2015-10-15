/**
 * 
 */
package edu.cmu.cs.lane.brokers;

import edu.cmu.cs.lane.brokers.load.CGELoadClinicalCenter;
import edu.cmu.cs.lane.brokers.load.CGELoadGeneticCenter;
import edu.cmu.cs.lane.brokers.preprocessing.ClinicalPreprocessorFactory;
import edu.cmu.cs.lane.brokers.preprocessing.GeneticPreprocessorFactory;
import edu.cmu.cs.lane.brokers.store.CGEStoreCenter;

/**
 * 
 * A static class that initializes all static brokers available in the system.
 * @author zinman
 *
 */
public class InitializeBrokers {

	static public void initializeStaticObjects(){
		MySQLConnector.initialize();
		CGEModelBroker.initialize();
		CGEStoreCenter.initialize();
		CGELoadGeneticCenter.initialize();
		CGELoadClinicalCenter.initialize();
		AnalysisSetBroker.initialize();
		GeneticPreprocessorFactory.initialize();
		ClinicalPreprocessorFactory.initialize();
		ClinicalDictionary.initialize();
	}
}
