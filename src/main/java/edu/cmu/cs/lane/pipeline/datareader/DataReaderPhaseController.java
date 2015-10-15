package edu.cmu.cs.lane.pipeline.datareader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsClassification;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadClinical;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;

public class DataReaderPhaseController extends PhaseController {

	private static final Logger logger = Logger
			.getLogger(DataReaderPhaseController.class);

	public void runPhase() {
		System.out.println("Running Phase 1 - deprecated");
		/*
		System.out.println("Running Phase 1");

		// Initialize objects
		logger.debug("Running phase 1 - data reader");

		HashMap<String, String> readerHash = new HashMap<String, String>();//TODO: should be taken from an external properties file
		readerHash.put("vcf", "VCFReader");
		readerHash.put("table", "SimpleTableReader");

		DataReaderPhaseClassFactory<AbstractDataReader> dataReaderFactory = new DataReaderPhaseClassFactory<AbstractDataReader>();
		dataReaderFactory.initialize("readers");

		DataReaderPhaseClassFactory<AbstractDataFilter> dataFilterFactory = new DataReaderPhaseClassFactory<AbstractDataFilter>();
		dataFilterFactory.initialize("filters");

		String type = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFormat();
		String dataLocation = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFolder();
		String filenamePattern = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFilePattern();
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFilePatternZipped() != null) {
			filenamePattern = ((OptionsGeneral) OptionsFactory.getOptions("general")).getGeneticInputFilePatternZipped(); //TODO: apply to individual readers
		}
		String outputFolder = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder() + "phase1" + File.separator;

		String tmp = outputFolder + File.separator + "tmp";
		File fileTmp = new File(tmp);
		fileTmp.mkdirs();
		fileTmp.delete();

		ArrayList<AbstractDataFilter> dataFilters = new ArrayList<AbstractDataFilter>();
		for (String filter : ((OptionsGeneral) OptionsFactory.getOptions("general")).getFilters()) {
			System.out.println("\tUsing Filter: " + filter);
			dataFilters.add(dataFilterFactory.create(filter));
		}
		AbstractDataReader reader = dataReaderFactory.create(readerHash.get(type));

		reader.read(dataFilters);
		//TODO: need to pass the right outputFolder
		//reader.read(dataLocation, filenamePattern, outputFolder, dataFilters, options);

		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("classification")) { //TODO: need to change to OptionsClassification
			type = ((OptionsGeneral) OptionsFactory.getOptions("general")).getBackgroundInputFormat();
			dataLocation = ((OptionsGeneral) OptionsFactory.getOptions("general")).getBackgroundInputFolder();
			filenamePattern = ((OptionsLoadGenetic) OptionsFactory.getOptions("loadGenetic")).getBackgroundInputFilePattern();
			reader = dataReaderFactory.create(readerHash.get(type));
			reader.read(dataFilters);
		}
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("regression")) { //TODO: need to change to OptionsGFLasso
			AbstractDataReader clinicalReader = dataReaderFactory.create("ClinicalTableReader");
			dataLocation = ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputFolder();
			filenamePattern = ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputFilePattern();
			clinicalReader.read( dataFilters);
		}
		*/
	}

}
