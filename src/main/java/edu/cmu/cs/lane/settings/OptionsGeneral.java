
package edu.cmu.cs.lane.settings;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class OptionsGeneral extends AbstractOptions {
	
	@Override
	public String getName() {
		return "general";
	}
	
	private String phases = ""; 
	private String type = "";
	private String analysisName = "";

	/**
	 * @return the analysisName
	 */
	public String getAnalysisName() {
		return analysisName;
	}




	/**
	 * @param analysisName the analysisName to set
	 */
	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}
	
	private String loadGeneticSource;

	private String[] storeTargets;
	private boolean useBackground = false;

	

	private String genetic_input_orientation;
	private String genetic_input_file_pattern;
	private String genetic_input_file_pattern_zipped;
	private String genetic_input_folder;
	private String genetic_input_format;
	
	private String background_input_folder;
	private String background_input_format;
	private String background_genetic_input_orientation;

	/**
	 * @return the background_genetic_input_orientation
	 */
	public String getBackgroundGeneticInputOrientation() {
		return background_genetic_input_orientation;
	}




	/**
	 * @param background_genetic_input_orientation the background_genetic_input_orientation to set
	 */
	public void setBackgroundGeneticInputOrientation(
			String background_genetic_input_orientation) {
		this.background_genetic_input_orientation = background_genetic_input_orientation;
	}

	private String output_folder;
	private Boolean addTimestampToOutput = true;
	/**
	 * @return the addTimestampToOutput
	 */
	public Boolean getAddTimestampToOutput() {
		return addTimestampToOutput;
	}




	/**
	 * @param addTimestampToOutput the addTimestampToOutput to set
	 */
	public void setAddTimestampToOutput(Boolean addTimestampToOutput) {
		this.addTimestampToOutput = addTimestampToOutput;
	}

	private String working_folder;
	private int missing_value = 3;

	private boolean verbose = true;
	private boolean debugMode = false;

	
	// Shotgun
	private double cv_percent = 0;
	private double testSet_percent = 0;
	/**
	 * @return the testSet_percent
	 */
	public double getTestSetPercent() {
		return testSet_percent;
	}




	/**
	 * @param testSet_percent the testSet_percent to set
	 */
	public void setTestSetPercent(double testSet_percent) {
		this.testSet_percent = testSet_percent;
	}

	private boolean permute_y;
	private String target;
	private String background;

	// Classification
	/*
	private double max_missing_freq;
	private double min_variability_freq;
	private String extractor_type;
	private String snp_file_pattern;
	private String snp_file_placeholder;
	private String stat_file_placeholder;
	private boolean stat_file_available;
	private double sig_cutoff;

	private String patient_remover_classpath;
	private String missing_value_imputer_classpath;
	private double missing_value_cutoff;
	private double sample_cutoff;
	private double similarity_cutoff;
	private String target_patient_remove_chromosome;
	private String background_patient_remove_chromosome;
*/
	private String[] extractors;
	private String[] filters;
	private String[] readers;
	private String[] analyzers;





	public void readParams(PropertiesConfiguration config) throws ConfigurationException {

		useBackground = false;
		verbose = false;
		if (config.containsKey("loadGeneticSource")) {
			loadGeneticSource = config.getString("loadGeneticSource");
		}


		if (config.containsKey("storeTargets")){
			String storeTargetsTemp = config.getString("storeTargets");
			storeTargets = storeTargetsTemp.split("[\\s,;]+");
		}

		if (config.containsKey("useBackground")) {
			this.setUseBackground(Boolean.parseBoolean(config.getProperty("useBackground")
					.toString()));
		}
		if (config.containsKey("analysis-cv-percent")) {
			this.setCvPercent(Double.parseDouble(config.getProperty(
					"analysis-cv-percent").toString()));
		}
		if (config.containsKey("test-set-percent")) {
			this.setTestSetPercent(Double.parseDouble(config.getProperty(
					"test-set-percent").toString()));
		}
		if (config.containsKey("analysis-type")) {
			this.setType((config.getProperty("analysis-type").toString()));
		}
		if (config.containsKey("verbose")) {
			this.setVerbose(Boolean.parseBoolean(config.getProperty("verbose")
					.toString()));
		}
		if (config.containsKey("debugModel")) {
			this.setVerbose(Boolean.parseBoolean(config.getProperty("debugMode")
					.toString()));
		}
		if (config.containsKey("analysis-permute-y")) {
			this.setPermuteY(Boolean.parseBoolean(config.getProperty(
					"analysis-permute-y").toString()));
		}

		if (config.containsKey("genetic-input-orientation")) {
			this.setGeneticInputOrientation((config
					.getProperty("genetic-input-orientation").toString()));
		}
		if (config.containsKey("background-genetic-input-orientation")) {
			this.setBackgroundGeneticInputOrientation((config
					.getProperty("background=genetic-input-orientation").toString()));
		}
		if (config.containsKey("genetic-input-file-pattern-zipped")) {
			this.setGeneticInputFilePatternZipped((config
					.getProperty("genetic-input-file-pattern-zipped")
					.toString()));
		}
		if (config.containsKey("genetic-input-file-pattern")) {
			this.setGeneticInputFilePattern((config
					.getProperty("genetic-input-file-pattern").toString()));
		}
		if (config.containsKey("genetic-input-folder")) {
			this.setGeneticInputFolder((config
					.getProperty("genetic-input-folder").toString()));
		}
		if (config.containsKey("genetic-input-format")) {
			this.setGeneticInputFormat((config
					.getProperty("genetic-input-format").toString()));
		}
		if (config.containsKey("output-folder")) {
			this.setOutputFolder((config.getProperty("output-folder").toString()));
		}
		if (config.containsKey("add-timestamp-to-output")) {
			this.setAddTimestampToOutput(Boolean.parseBoolean(config.getProperty("add-timestamp-to-output").toString()));
		}
		if (config.containsKey("working-folder")) {
			this.setWorkingFolder((config.getProperty("working-folder")
					.toString()));
		}
		if (config.containsKey("background-input-folder")) {
			this.setBackgroundInputFolder((config
					.getProperty("background-input-folder").toString()));
		}
		if (config.containsKey("analysis-name")) {
			this.setAnalysisName((config.getProperty("analysis-name").toString()));
		}
		if (config.containsKey("phases")) {
			this.setPhases((config.getProperty("phases").toString()));
		}
		if (config.containsKey("target")) {
			this.setTarget((config.getProperty("target").toString()));
		}
		if (config.containsKey("background")) {
			this.setBackground((config.getProperty("background").toString()));
		}
		if (config.containsKey("background-input-format")) {
			this.setBackgroundInputFormat((config
					.getProperty("background-input-format").toString()));
		}
		
		if (config.containsKey("missing-value-symbol")) {
			this.setMissingValueSymbol(Integer.parseInt(config.getProperty("missing-value-symbol").toString()));
		}
		
		/*
		if (config.containsKey("max-missing-freq")) {
			this.setMaxMissingFreq(Double.parseDouble(config.getProperty(
					"max-missing-freq").toString()));
		}
		if (config.containsKey("min-variability-freq")) {
			this.setMinVariabilityFreq(Double.parseDouble(config.getProperty(
					"min-variability-freq").toString()));
		}
		if (config.containsKey("extractor-type")) {
			this.setExtractorType((config.getProperty("extractor-type")
					.toString()));
		}
		if (config.containsKey("snp-file-pattern")) {
			this.setSnpFilePattern((config.getProperty("snp-file-pattern")
					.toString()));
		}
		if (config.containsKey("snp-file-placeholder")) {
			this.setSnpFilePlaceholder((config
					.getProperty("snp-file-placeholder").toString()));
		}
		if (config.containsKey("stat-file-placeholder")) {
			this.setStatFilePlaceholder((config
					.getProperty("stat-file-placeholder").toString()));
		}
		if (config.containsKey("stat-file-available")) {
			this.setStatFileAvailable(Boolean.parseBoolean(config
					.getProperty("stat-file-available").toString()));
		}
		if (config.containsKey("sig-cutoff")) {
			this.setSigCutoff(Double.parseDouble(config
					.getProperty("sig-cutoff").toString()));
		}
		if (config.containsKey("patient-remover-classpath")) {
			this.setPatientRemoverClasspath((config
					.getProperty("patient-remover-classpath").toString()));
		}
		if (config.containsKey("missing-value-imputer-classpath")) {
			this.setMissingValueImputerClasspath((config
					.getProperty("missing-value-imputer-classpath").toString()));
		}
		if (config.containsKey("missing-value-cutoff")) {
			this.setMissingValueCutoff(Double.parseDouble(config.getProperty(
					"missing-value-cutoff").toString()));
		}
		if (config.containsKey("sample-cutoff")) {
			this.setSampleCutoff(Double.parseDouble(config.getProperty(
					"sample-cutoff").toString()));
		}
		if (config.containsKey("similarity-cutoff")) {
			this.setSimilarityCutoff(Double.parseDouble(config.getProperty(
					"similarity-cutoff").toString()));
		}
		if (config.containsKey("target-patient-remove-chromosome")) {
			this.setTargetPatientRemoveChromosome((config
					.getProperty("target-patient-remove-chromosome").toString()));
		}
		if (config.containsKey("background-patient-remove-chromosome")) {
			this.setBackgroundPatientRemoveChromosome((config
					.getProperty("background-patient-remove-chromosome")
					.toString()));
		}
		if (config.containsKey("window-size")) {
			this.setWindowSize(Integer.parseInt(config
					.getProperty("window-size").toString()));
		}
		*/
		
		
		if (config.containsKey("analysis-analyzers")) {
			this.setAnalyzers((config.getProperty("analysis-analyzers")
					.toString().split("[\\s,;]+")));
		}
		if (this.getAnalyzers() != null) {
			String[] analyzers = this.getAnalyzers();

			for (String analyzer : analyzers) {
				OptionsFactory.lazyLoadClasses(analyzer);
			}
		}

		if (config.containsKey("analysis-readers")) {
			this.setReaders((config.getProperty("analysis-readers").toString()
					.split("[\\s,;]+")));
		}

		if (config.containsKey("readers")) {
			String[] strReaders = config.getStringArray("readers");
			if (strReaders.length > 0) {
				readers = new String[strReaders.length];
				for (int i = 0; i < strReaders.length; i++) {
					setReader(i, strReaders[i]);
				}
			}
		}
		if (config.containsKey("extractors")) {
			String[] strExtractors = config.getStringArray("extractors");
			if (strExtractors.length > 0) {
				extractors = new String[strExtractors.length];
				for (int i = 0; i < strExtractors.length; i++) {
					setExtractor(i, strExtractors[i]);
				}
			}
		}
		if (config.containsKey("filters")) {
			String[] strFilters = config.getStringArray("filters");
			if (strFilters.length > 0) {
				filters = new String[strFilters.length];
				for (int i = 0; i < strFilters.length; i++) {
					setFilter(i, strFilters[i]);
				}
			}
		}
		if (config.containsKey("analyzers")) {
			String[] strAnalyzers = config.getStringArray("analyzers");
			if (strAnalyzers.length > 0) {
				analyzers = new String[strAnalyzers.length];
				for (int i = 0; i < strAnalyzers.length; i++) {
					setAnalyzer(i, strAnalyzers[i]);
				}
			}
		}
	}






	/**
	 * @return the useBackground
	 */
	public boolean isUseBackground() {
		return useBackground;
	}




	/**
	 * @param useBackground the useBackground to set
	 */
	public void setUseBackground(boolean useBackground) {
		this.useBackground = useBackground;
	}




	public String getType() {
		if (type == null) {
			System.err.println("Warning 'type' is null!");
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	public String getGeneticInputOrientation() {
		if (genetic_input_orientation == null) {
			genetic_input_orientation = "rows-as-patients";
			System.err.println("Warning: 'genetic_input_orientation' is null - assumption - rows-as-patients");
		}
		return genetic_input_orientation;
	}

	public void setGeneticInputOrientation(String genetic_input_orientation) {
		this.genetic_input_orientation = genetic_input_orientation;
	}

	public String getGeneticInputFilePattern() {
		if (genetic_input_file_pattern == null) {
			System.err.println("Warning 'genetic_input_file_pattern' is null!");
		}
		return genetic_input_file_pattern;
	}

	public void setGeneticInputFilePattern(String genetic_input_file_pattern) {
		this.genetic_input_file_pattern = genetic_input_file_pattern;
	}

	public String getGeneticInputFolder() {
		if (genetic_input_folder == null) {
			System.err.println("Warning 'genetic_input_folder' is null!");
		}
		return genetic_input_folder;
	}

	public void setGeneticInputFolder(String genetic_input_folder) {
		this.genetic_input_folder = genetic_input_folder;
	}

	public String getGeneticInputFormat() {
		if (genetic_input_format == null) {
			System.err.println("Warning 'genetic_input_format' is null!");
		}
		return genetic_input_format;
	}

	public void setGeneticInputFormat(String genetic_input_format) {
		this.genetic_input_format = genetic_input_format;
	}

	public String getOutputFolder() {
		if (output_folder == null) {
			System.err.println("Warning 'output_folder' is null!");
		}
		return output_folder;
	}

	public void setOutputFolder(String output_folder) {
		this.output_folder = output_folder;
	}

	public String getWorkingFolder() {
		if (working_folder == null) {
			System.err.println("Warning 'working_folder' is null!");
		}
		return working_folder;
	}

	public void setWorkingFolder(String working_folder) {
		this.working_folder = working_folder;
	}

	public String getPhases() {
		if (phases == null) {
			System.err.println("Warning 'phases' is null!");
		}
		return phases;
	}

	public void setPhases(String phases) {
		this.phases = phases;
	}


	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		System.out.println("Setting verbose to: "
				+ ((verbose) ? "true" : "false"));
		this.verbose = verbose;
	}
	
	public double getCvPercent() {
		return cv_percent;
	}

	public void setCvPercent(double cv_percent) {
		this.cv_percent = cv_percent;
	}

	public boolean isPermuteY() {
		return permute_y;
	}

	public void setPermuteY(boolean permute_y) {
		this.permute_y = permute_y;
	}



	public String getTarget() {
		if (target == null) {
			System.err.println("Warning 'target' is null!");
		}
		return target;
	}

	public String getBackground() {
		if (background == null) {
			System.err.println("Warning 'background' is null!");
		}
		return background;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getBackgroundInputFolder() {
		if (background_input_folder == null) {
			System.err.println("Warning 'background_input_folder' is null!");
		}
		return background_input_folder;
	}

	public void setBackgroundInputFolder(String background_input_folder) {
		this.background_input_folder = background_input_folder;
	}

	public String getBackgroundInputFormat() {
		if (background_input_format == null) {
			System.err.println("Warning 'background_input_format' is null!");
		}
		return background_input_format;
	}

	public void setBackgroundInputFormat(String background_input_format) {
		this.background_input_format = background_input_format;
	}




	public int getMissingValueSymbol() {
		if (missing_value == 0) {
			System.err.println("Warning 'missing-value' should not be zero!");
			System.err.println("Exiting.");
			System.exit(1);
		}
		return missing_value;
	}

	public void setMissingValueSymbol(int missing_value) {
		this.missing_value = missing_value;
	}

	public String getGeneticInputFilePatternZipped() {
		return genetic_input_file_pattern_zipped;
	}

	public void setGeneticInputFilePatternZipped(
			String genetic_input_file_pattern_zipped) {
		this.genetic_input_file_pattern_zipped = genetic_input_file_pattern_zipped;
	}

	/*
	public double getMaxMissingFreq() {
		return max_missing_freq;
	}

	public void setMaxMissingFreq(double max_missing_freq) {
		this.max_missing_freq = max_missing_freq;
	}

	public double getMinVariabilityFreq() {
		return min_variability_freq;
	}

	public void setMinVariabilityFreq(double min_variability_freq) {
		this.min_variability_freq = min_variability_freq;
	}

	public String getLookupListFolder() {
		// TODO Auto-generated method stub
		System.err.println("Warning 'getLookupListFolder' not completed!");
		return null;
	}

	public String getFilenamePattern() {
		// TODO Auto-generated method stub
		System.err.println("Warning 'getFilenamePattern' not completed!");
		return null;
	}

	public String getFilterBy() {
		// TODO Auto-generated method stub
		System.err.println("Warning 'getFilterBy' not completed!");
		return null;
	}

	public String getExtractorType() {
		if (extractor_type == null) {
			System.err.println("Warning 'extractor_type' is null!");
		}
		return extractor_type;
	}

	public void setExtractorType(String extractor_type) {
		this.extractor_type = extractor_type;
	}

	public String getSnpFilePattern() {
		if (snp_file_pattern == null) {
			System.err.println("Warning 'snp_file_pattern' is null!");
		}
		return snp_file_pattern;
	}

	public void setSnpFilePattern(String snp_file_pattern) {
		this.snp_file_pattern = snp_file_pattern;
	}

	public String getSnpFilePlaceholder() {
		if (snp_file_placeholder == null) {
			System.err.println("Warning 'snp_file_placeholder' is null!");
		}
		return snp_file_placeholder;
	}

	public void setSnpFilePlaceholder(String snp_file_placeholder) {
		this.snp_file_placeholder = snp_file_placeholder;
	}

	public String getStatFilePlaceholder() {
		if (stat_file_placeholder == null) {
			System.err.println("Warning 'stat_file_placeholder' is null!");
		}
		return stat_file_placeholder;
	}

	public void setStatFilePlaceholder(String stat_file_placeholder) {
		this.stat_file_placeholder = stat_file_placeholder;
	}

	public double getSigCutoff() {
		if (sig_cutoff <= 0) {
			System.err
					.println("Warning 'sig_cutoff' should be greater than zero!");
		}
		return sig_cutoff;
	}

	public void setSigCutoff(double sig_cutoff) {
		this.sig_cutoff = sig_cutoff;
	}

	public boolean isStatFileAvailable() {
		return stat_file_available;
	}

	public void setStatFileAvailable(boolean stat_file_available) {
		this.stat_file_available = stat_file_available;
	}

	public String getPatientRemoverClasspath() {
		if (patient_remover_classpath == null) {
			System.err.println("Warning 'patient_remover_classpath' is null!");
		}
		return patient_remover_classpath;
	}

	public void setPatientRemoverClasspath(String patient_remover_classpath) {
		this.patient_remover_classpath = patient_remover_classpath;
	}

	public String getMissingValueImputerClasspath() {
		if (missing_value_imputer_classpath == null) {
			System.err
					.println("Warning 'missing_value_imputer_classpath' is null!");
		}
		return missing_value_imputer_classpath;
	}

	public void setMissingValueImputerClasspath(
			String missing_value_imputer_classpath) {
		this.missing_value_imputer_classpath = missing_value_imputer_classpath;
	}

	public double getMissingValueCutoff() {
		if (missing_value_cutoff <= 0) {
			System.err
					.println("Warning 'missing_value_cutoff' should be greater than zero!");
		}
		return missing_value_cutoff;
	}

	public void setMissingValueCutoff(double missing_value_cutoff) {
		this.missing_value_cutoff = missing_value_cutoff;
	}

	public double getSampleCutoff() {
		return sample_cutoff;
	}

	public void setSampleCutoff(double sample_cutoff) {
		this.sample_cutoff = sample_cutoff;
	}

	public double getSimilarityCutoff() {
		return similarity_cutoff;
	}

	public void setSimilarityCutoff(double similarity_cutoff) {
		this.similarity_cutoff = similarity_cutoff;
	}

	public String getTargetPatientRemoveChromosome() {
		if (target_patient_remove_chromosome == null) {
			System.err
					.println("Warning 'target_patient_remove_chromosome' is null!");
		}
		return target_patient_remove_chromosome;
	}

	public void setTargetPatientRemoveChromosome(
			String target_patient_remove_chromosome) {
		this.target_patient_remove_chromosome = target_patient_remove_chromosome;
	}

	public String getBackgroundPatientRemoveChromosome() {
		if (background_patient_remove_chromosome == null) {
			System.err
					.println("Warning 'background_patient_remove_chromosome' is null!");
		}
		return background_patient_remove_chromosome;
	}

	public void setBackgroundPatientRemoveChromosome(
			String background_patient_remove_chromosome) {
		this.background_patient_remove_chromosome = background_patient_remove_chromosome;
	}

	public int getWindowSize() {
		return window_size;
	}

	public void setWindowSize(int window_size) {
		this.window_size = window_size;
	}
	*/

	public String[] getAnalyzers() {
		return analyzers;
	}

	public void setAnalyzers(String[] analyzers) {
		this.analyzers = analyzers;
	}


	public String[] getReaders() {
		if (readers == null) {
			System.err.println("Warning 'readers' is null!");
		}
		return readers;
	}

	public void setReaders(String[] readers) {
		this.readers = readers;
	}
	
	public String[] getFilters() {
		if (filters == null) {
			System.err.println("Warning 'filters' is null!");
		}
		return filters;
	}

	public void setFilters(String[] filters) {
		this.filters = filters;
	}

	public String[] getExtractors() {
		if (extractors == null) {
			System.err.println("Warning 'extractors' is null!");
		}
		return extractors;
	}

	public void setExtractors(String[] extractors) {
		this.extractors = extractors;
	}
	private void setAnalyzer(int i, String string) {
		analyzers[i] = string;
	}

	private void setFilter(int i, String string) {
		filters[i] = string;
	}

	private void setExtractor(int i, String string) {
		extractors[i] = string;
	}

	private void setReader(int i, String string) {
		readers[i] = string;
	}

	/**
	 * @return the loadSources
	 */
	public String getLoadGeneticSource() {
		return loadGeneticSource;
	}

	/**
	 * @param loadSources the loadSources to set
	 */
	public void setLoadGeneticSource(String source) {
		this.loadGeneticSource = source;
	}


	
	/**
	 * @return the storeTargets
	 */
	public String[] getStoreTargets() {
		return storeTargets;
	}

	/**
	 * @param storeTargets the storeTargets to set
	 */
	public void setStoreTargets(String[] storeTargets) {
		this.storeTargets = storeTargets;
	}




	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}




	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}


	
}