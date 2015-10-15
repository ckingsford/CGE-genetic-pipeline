package edu.cmu.cs.lane.offlinetasks.matchIDs;

import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;

import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;

public class RunPhase0DataExtractor {
    public static void main(String[] args) throws ConfigurationException {
	
	if (args.length > 0) {
		Hashtable<String, String> propertiesOverrides = new Hashtable<String, String>();
		if (args.length > 1){
			for (int i = 1; i< args.length; i+=2){
				propertiesOverrides.put(args[i], args[i+1]);
			}
		}
		OptionsFactory.initialize(args[0], propertiesOverrides);
	    System.out.println("Reading properties: " + args[0]);
	} else {
	    System.out
		    .println("Please provide properties file as a parameter.");
	    System.exit(1);
	}
	PhaseController controller = new DataPreparationPhaseController();
	controller.runPhase();
    }
}
