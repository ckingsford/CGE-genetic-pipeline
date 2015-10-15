package edu.cmu.cs.lane.offlinetasks.matchIDs;

import org.apache.log4j.Logger;

import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class DataPreparationPhaseController extends PhaseController {
	private static final Logger logger = Logger
			.getLogger(DataPreparationPhaseController.class.getName());

	@Override
	public void runPhase() {
		System.out.println("Running Phase 0");
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("regression")) {
			return;
		}
		logger.debug("Running phase 0 - data extractor");

		DataExtractorFactory.initialize();
		String type = "extractor";//TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getExtractorType();
		String diseaseDataLocation = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFolder();
		String thousandgenomeDataLocation = ((OptionsGeneral) OptionsFactory.getOptions("general")).getBackgroundInputFolder();
		String filenamePattern = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFilePattern();
		String outputFolder = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder();

		AbstractDataExtractor extractor = DataExtractorFactory.create(type);

		extractor.read(diseaseDataLocation, thousandgenomeDataLocation,
				filenamePattern, outputFolder);

	}

}
