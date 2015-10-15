package edu.cmu.cs.lane.pipeline.datapreprocessor.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.AbstractGeneticMissingValueImputer;
import edu.cmu.cs.lane.pipeline.datapreprocessor.patient.AbstractSampleFilter;
import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.pipeline.util.FactoryUtils;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadClinical;
import edu.cmu.cs.lane.settings.OptionsPreprocessing;

public class DataPreprocessorPhaseController extends PhaseController {

	private static final Logger logger = Logger
			.getLogger(DataPreprocessorPhaseController.class.getName());
	private AbstractSampleFilter patientRemover;
	private AbstractGeneticMissingValueImputer imputer;
	@Override
	public void runPhase() {
		System.out.println("Running Phase 2");

		String targetInFolderName = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder() + "phase1"
				+ File.separator + "target" + File.separator;
		String targetOutFolderName = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder() + "phase2"
				+ File.separator + "target" + File.separator;
		String backgroundInFolderName = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder() + "phase1"
				+ File.separator + "background" + File.separator;
		String backgroundOutFolderName = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder() + "phase2"
				+ File.separator + "background" + File.separator;
		String backgroundPatientRemoveChromosome = "1"; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getBackgroundPatientRemoveChromosome();
		String backgroundChrNumStr =  backgroundPatientRemoveChromosome;

		try {
			
			String patientRemoverClassPath = "class"; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getPatientRemoverClasspath();
			String missingValueImputerClassPath = "class"; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueImputerClasspath();

			double missingValueCutoff = 0.5;//TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueCutoff();
			double sampleCutoff = 0.5;//TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getSampleCutoff();
			double simialrityCutoff = 0.5;//TODO:((OptionsGeneral) OptionsFactory.getOptions("general")).getSimilarityCutoff();

			/*
			patientRemover = FactoryUtils.<AbstractSampleFilter> classInstantiator(patientRemoverClassPath);
			patientRemover.setMissingDataCutoff(missingValueCutoff);
			patientRemover.setSampleCutoff(sampleCutoff);
			patientRemover.setSimilarityCutoff(simialrityCutoff);
*/
			
			
			int windowSize = ((OptionsPreprocessing) OptionsFactory.getOptions("preprocessing")).getWindowSize();

			imputer = FactoryUtils.<AbstractGeneticMissingValueImputer> classInstantiator(missingValueImputerClassPath);
			imputer.setWindowSize(windowSize);

			File tmp1 = new File(targetOutFolderName);
			tmp1.mkdirs();

			String targetPatRmChrmStr = "1"; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getTargetPatientRemoveChromosome();
			String snpFilePlaceholder = "file"; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getSnpFilePlaceholder()
			try {
				processData(targetInFolderName, targetOutFolderName,
						snpFilePlaceholder,
						Integer.parseInt(targetPatRmChrmStr));
			} catch (IOException ioe) {
				ioe.printStackTrace();
				logger.error("", ioe);
			}

			if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("classification")) {
				File tmp2 = new File(backgroundOutFolderName);
				tmp2.mkdirs();
				try {
					processData(backgroundInFolderName,
							backgroundOutFolderName,
							snpFilePlaceholder,
							Integer.parseInt(backgroundChrNumStr));
				} catch (IOException ioe) {
					ioe.printStackTrace();
					logger.error("", ioe);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}

	}

	private void processData(String inFolderName, String outFolderName,
			String dataFileNamePlaceHolder, int chrNumStr)
			throws IOException {
		// System.out.println("== processData ==");
		// finding patients to remove in target or background
		// one chromosome is enough.

		List<String> patientsToRemove = null;
		if (dataFileNamePlaceHolder != null) {
			/*
			patientsToRemove = patientRemover.findPatientsToRemove(inFolderName
					+ File.separator
					+ String.format(dataFileNamePlaceHolder, chrNumStr));
			*/
		}
		File inFolder = new File(inFolderName);
		if (!inFolder.exists()) {
			// System.err.println("WARNING: Folder not found: " + inFolderName);
			return;
		}
		File outFolder = new File(outFolderName);
		outFolder.mkdirs();
		File[] inFiles = inFolder.listFiles();

		Pattern yFilePattern = null;
		Pattern inFilePattern = Pattern.compile(((OptionsGeneral) OptionsFactory.getOptions("general"))
				.getGeneticInputFilePattern());
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("regression")) {
			yFilePattern = Pattern.compile(((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getClinicalInputFilePattern());
		}
		Matcher matcher;

		// impute target missing data, write result into file
		int num = 0;
		for (File inFile : inFiles) {
			matcher = inFilePattern.matcher(inFile.getName());
			if (matcher.find()) {
				System.out.println("\tFile selected: " + inFile);
				File outFile = new File(outFolderName + File.separator
						+ inFile.getName());
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				//imputer.imputeMissingValue(inFile, outFile, patientsToRemove);
				num++;
			}
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("regression")) {
				matcher = yFilePattern.matcher(inFile.getName());
				if (matcher.find()) {
					File outFile = new File(outFolderName + File.separator
							+ inFile.getName());
					if (!outFile.exists()) {
						outFile.createNewFile();
					}
					removePatients(inFile, outFile, patientsToRemove);
					num++;
				}
			}
		}

		if (num == 0) {
			System.err.println("WARNING: No files found to process in: "
					+ inFolderName);
		}
	}

	private void removePatients(File inFile, File outFile,
			List<String> patientsToRemove) {
		BufferedReader bfr = null;
		BufferedWriter bout = null;
		try {
			bfr = new BufferedReader(new FileReader(inFile));
			bout = new BufferedWriter(new FileWriter(outFile));
			String str = bfr.readLine();
			String[] header = str.split("\\s+");
			for (int i = 0; i < header.length; i++) {
				if (!patientsToRemove.contains(header[i])) {
					bout.write(header[i] + "\t");
				}
			}
			bout.write("\n");
			String[] tokens = str.split("\\s+");
			while ((str = bfr.readLine()) != null) {
				tokens = str.split("\\s+");

				for (int i = 0; i < tokens.length; i++) {
					// jumping over removed patients
					if (!patientsToRemove.contains(header[i])) {
						bout.write(tokens[i] + "\t");
					}
				}
				bout.write("\n");

			}

		} catch (IOException e) {
			logger.error("", e);
		} finally {
			try {
				bfr.close();
				bfr = null;
			} catch (IOException e) {
				logger.error("", e);
			}
			try {
				bout.close();
				bout = null;
			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}

}
