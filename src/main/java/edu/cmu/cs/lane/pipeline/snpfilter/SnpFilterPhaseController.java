package edu.cmu.cs.lane.pipeline.snpfilter;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class SnpFilterPhaseController extends PhaseController {

	private static final Logger logger = Logger
			.getLogger(SnpFilterPhaseController.class);

	private SnpFilter filter;

	@Override
	public void runPhase() {
		System.out.println("Running Phase 3");

		String targetInFolderName = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()
				+ "phase2/target/";
		String targetOutFolderName = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()
				+ "phase3/target/";

		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getType().equalsIgnoreCase("regression")) {
			// Just copy between phases for regression
			File inFolder = new File(targetInFolderName);
			if (!inFolder.exists()) {
				System.err.println("WARNING: Folder not found: " + targetInFolderName);
				return;
			}
			File outFolder = new File(targetOutFolderName);
			outFolder.mkdirs();
			File[] inFiles = inFolder.listFiles();
			for (File inFile : inFiles) {
				if (inFile.getName().endsWith("~")) {
					continue;
				}
				try {
					File outFile = new File(outFolder + File.separator
							+ inFile.getName());
					FileUtils.copyFile(inFile, outFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return;
		}

		String snpFileNamePatten = "snpFileNamePatten";//TODO: ((OptionsGeneral) OptionsFactory.getOptions("general")).getSnpFilePattern();

		filter = new SimpleSnpFilter();

		// find all chromosomes
		File targetInFolder = new File(targetInFolderName);
		File[] targetInFiles = targetInFolder.listFiles();
		Pattern pattern = Pattern.compile(snpFileNamePatten);
		Matcher matcher;
		String chromosomeNumberStr;
		int num = 0;
		for (File targetInFile : targetInFiles) {
			if (targetInFile.getName().endsWith("~")) {
				continue;
			}
			String fileName = targetInFile.getName();
			logger.debug("processing file: " + fileName);
			matcher = pattern.matcher(fileName);
			if (matcher.find()) {
				System.out.println("\tFile selected: " + targetInFile);
				chromosomeNumberStr = matcher.group(1);
				filter.filterSnps(chromosomeNumberStr);
			}
			num++;
		}
		if (num == 0) {
			System.err.println("WARNING: No files found to process in: "
					+ targetInFolder);
		}
	}

	public static void main(String args[]) {
		try {
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
			SnpFilterPhaseController main = new SnpFilterPhaseController();
			main.runPhase();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
