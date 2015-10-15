/**
 * 
 */
package edu.cmu.cs.lane.settings;

import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import edu.cmu.cs.lane.pipeline.datareader.clinicalreader.AbstractClinicalFileDataReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.AbstractGeneticFileDataReader;

/**
 * @author zinman
 *
 */
public class OptionsLoadClinical extends AbstractOptions {

	private String loadClinicalSource;
	private String clinical_input_file_pattern;
	private String clinical_input_folder;
	private String clinical_input_format;
	private String clinical_input_orientation;
	
	private String background_loadClinicalSource;
	private String background_clinical_input_file_pattern;
	private String background_clinical_input_folder;
	private String background_clinical_input_format;
	private String background_clinical_input_orientation;
	
	private Hashtable<String, AbstractClinicalFileDataReader> extensionsHash = new Hashtable<String, AbstractClinicalFileDataReader>();

	
	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "loadClinical";
	}
	
	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config)
			throws ConfigurationException {
		
		if (config.containsKey("loadClinicalSource")) {
			loadClinicalSource = config.getString("loadClinicalSource");
		}
		if (config.containsKey("clinical-input-file-pattern")) {
			this.setClinicalInputFilePattern((config
					.getProperty("clinical-input-file-pattern").toString()));
		}
		if (config.containsKey("clinical-input-folder")) {
			this.setClinicalInputFolder((config
					.getProperty("clinical-input-folder").toString()));
		}
		if (config.containsKey("clinical-input-format")) {
			this.setClinicalInputFormat((config
					.getProperty("clinical-input-format").toString()));
		}
		if (config.containsKey("clinical-input-orientation")) {
			this.setClinicalInputOrientation((config
					.getProperty("clinical-input-orientation").toString()));
		}

		if (config.containsKey("loadClinicalSource-background")) {
			loadClinicalSource = config.getString("loadClinicalSource-background");
		}
		if (config.containsKey("clinical-input-file-pattern-background")) {
			this.setClinicalInputFilePattern((config
					.getProperty("clinical-input-file-pattern-background").toString()));
		}
		if (config.containsKey("clinical-input-folder-background")) {
			this.setClinicalInputFolder((config
					.getProperty("clinical-input-folder-background").toString()));
		}
		if (config.containsKey("clinical-input-format-background")) {
			this.setClinicalInputFormat((config
					.getProperty("clinical-input-format-background").toString()));
		}
		if (config.containsKey("clinical-input-orientation-background")) {
			this.setClinicalInputOrientation((config
					.getProperty("clinical-input-orientation-background").toString()));
		}

	}
	
	public String getBackgroundClinicalInputFilePattern() {
		if (background_clinical_input_file_pattern == null) {
			System.err
					.println("Warning 'background_clinical_input_file_pattern' is null!");
		}
		return background_clinical_input_file_pattern;
	}

	public void setBackgroundClinicalInputFilePattern(String bg_clinical_input_file_pattern) {
		this.background_clinical_input_file_pattern = bg_clinical_input_file_pattern;
	}

	public String getBackgroundClinicalInputFolder() {
		if (background_clinical_input_folder == null) {
			System.err.println("Warning 'bg_clinical_input_folder' is null!");
		}
		return background_clinical_input_folder;
	}

	public void setBackgroundClinicalInputFolder(String bg_clinical_input_folder) {
		this.background_clinical_input_folder = bg_clinical_input_folder;
	}

	public String getBackgroundClinicalInputFormat() {
		if (background_clinical_input_format == null) {
			System.err.println("Warning 'background_clinical_input_format' is null!");
		}
		return background_clinical_input_format;
	}

	public void setBackgroundClinicalInputFormat(String bg_clinical_input_format) {
		this.background_clinical_input_format = bg_clinical_input_format;
	}

	public String getBackgroundClinicalInputOrientation() {
		if (background_clinical_input_orientation == null) {
			background_clinical_input_orientation = "rows-as-patients";
			System.err.println("Warning: 'background_clinical_input_orientation' is null - assumption: rows-as-patients");
		}
		return background_clinical_input_orientation;
	}

	public void setBackgroundClinicalInputOrientation(String bg_clinical_input_orientation) {
		this.background_clinical_input_orientation = bg_clinical_input_orientation;
	}

	/**
	 * @return the loadSources
	 */
	public String getBackgroundLoadClinicalSource() {
		return background_loadClinicalSource;
	}

	/**
	 * @param loadSources the loadSources to set
	 */
	public void setBackgroundLoadClinicalSource(String source) {
		this.background_loadClinicalSource = source;
	}
	
	public String getClinicalInputFilePattern() {
		if (clinical_input_file_pattern == null) {
			System.err
					.println("Warning 'clinical_input_file_pattern' is null!");
		}
		return clinical_input_file_pattern;
	}

	public void setClinicalInputFilePattern(String clinical_input_file_pattern) {
		this.clinical_input_file_pattern = clinical_input_file_pattern;
	}

	public String getClinicalInputFolder() {
		if (clinical_input_folder == null) {
			System.err.println("Warning 'clinical_input_folder' is null!");
		}
		return clinical_input_folder;
	}

	public void setClinicalInputFolder(String clinical_input_folder) {
		this.clinical_input_folder = clinical_input_folder;
	}

	public String getClinicalInputFormat() {
		if (clinical_input_format == null) {
			System.err.println("Warning 'clinical_input_format' is null!");
		}
		return clinical_input_format;
	}

	public void setClinicalInputFormat(String clinical_input_format) {
		this.clinical_input_format = clinical_input_format;
	}

	public String getClinicalInputOrientation() {
		if (clinical_input_orientation == null) {
			clinical_input_orientation = "rows-as-patients";
			System.err.println("Warning: 'clinical_input_orientation' is null - assumption: rows-as-patients");
		}
		return clinical_input_orientation;
	}

	public void setClinicalInputOrientation(String clinical_input_orientation) {
		this.clinical_input_orientation = clinical_input_orientation;
	}

	/**
	 * @return the loadSources
	 */
	public String getLoadClinicalSource() {
		return loadClinicalSource;
	}

	/**
	 * @param loadSources the loadSources to set
	 */
	public void setLoadClinicalSource(String source) {
		this.loadClinicalSource = source;
	}
	
	public void registerFileReader(AbstractClinicalFileDataReader reader, String[] extensions){
		for (int i=0; i<extensions.length; i++){
			extensionsHash.put(extensions[i].toLowerCase(), reader);
		}
	}
	
	public AbstractClinicalFileDataReader getReaderByFilename(String filename) {
		filename =  filename.toLowerCase();
		for (String extension:extensionsHash.keySet()){
			if (filename.endsWith(extension)) return extensionsHash.get(extension);
		}
		String error = "filename '" + filename + "' has a file extension that is not supported by the system";
		System.out.println(error);
		return null;
	}

}
