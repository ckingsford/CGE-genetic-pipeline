package edu.cmu.cs.lane.runner;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import edu.cmu.cs.lane.brokers.InitializeBrokers;
import edu.cmu.cs.lane.pipeline.dataanalyzer.DataAnalysisPhaseController;
import edu.cmu.cs.lane.pipeline.datapreprocessor.runner.DataPreprocessorPhaseController;
import edu.cmu.cs.lane.pipeline.datareader.DataReaderPhaseController;
import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.pipeline.snpfilter.SnpFilterPhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * Entry point to the pipeline
 * @author zinman
 *
 */

//package the project as a "fat jar"
//mvn -f genetic-pipeline/pom.xml clean compile assembly:single

public class RunPipeline {
	public static void main(String[] args) throws ConfigurationException, IOException { 

		if (args.length > 0) {
			System.out.println("Reading properties: " + args[0]);
			Hashtable<String, String> propertiesOverrides = new Hashtable<String, String>();
			if (args.length > 1){
				for (int i = 1; i< args.length; i+=2){
					propertiesOverrides.put(args[i], args[i+1]);
				}
			}
			OptionsFactory.initialize(args[0], propertiesOverrides);
			
		} else {
			System.out
					.println("Please provide properties file as a parameter.");
			System.exit(1);
		}
		InitializeBrokers.initializeStaticObjects();
		
		System.out.println("Analysis name: " + ((OptionsGeneral) OptionsFactory.getOptions("general")).getAnalysisName());
		PhaseController controller = new DataAnalysisPhaseController();
		controller.runPhase();
		
		/*
		List<PhaseController> phaseControllers = new LinkedList<PhaseController>();
		OptionsGeneral optionsGeneral = (OptionsGeneral) OptionsFactory.getOptions("general"); //pipeline
		
		if (optionsGeneral.getPhases() != null) {
			String[] phases = optionsGeneral.getPhases().split("[\\s,;]+");

			for (String phase : phases) {
				if (phase.equals("1")) {
					phaseControllers.add(new DataReaderPhaseController());
				}
				if (phase.equals("2")) {
					phaseControllers.add(new DataPreprocessorPhaseController());
				}
				if (phase.equals("3")) {
					phaseControllers.add(new SnpFilterPhaseController());
				}
				if (phase.equals("4")) {
					phaseControllers.add(new DataAnalysisPhaseController());
				}
			}
			for (PhaseController controller : phaseControllers) {
				controller.runPhase();
			}
		}else{
			System.out.println("WARNING: no pipeline phases to be run are defined.");
		}*/
	}
}
