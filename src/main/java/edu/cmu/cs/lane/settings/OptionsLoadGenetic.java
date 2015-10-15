/**
 * 
 */
package edu.cmu.cs.lane.settings;

import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import edu.cmu.cs.lane.pipeline.datareader.AbstractDataReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.AbstractGeneticFileDataReader;

/**
 * @author zinman
 *
 */
public class OptionsLoadGenetic extends AbstractOptions {

	private String genetic_input_file_pattern;
	private String background_genetic_input_file_pattern;
	private boolean storeCheckpoints = false;
	private Hashtable<String, AbstractGeneticFileDataReader> extensionsHash = new Hashtable<String, AbstractGeneticFileDataReader>();

	
	/**
	 * @return the genetic_input_file_pattern
	 */
	public String getGenetic_input_file_pattern() {
		return genetic_input_file_pattern;
	}

	/**
	 * @param genetic_input_file_pattern the genetic_input_file_pattern to set
	 */
	public void setGenetic_input_file_pattern(String genetic_input_file_pattern) {
		this.genetic_input_file_pattern = genetic_input_file_pattern;
	}

	/**
	 * @return the background_genetic_input_file_pattern
	 */
	public String getBackground_genetic_input_file_pattern() {
		return background_genetic_input_file_pattern;
	}

	/**
	 * @param background_genetic_input_file_pattern the background_genetic_input_file_pattern to set
	 */
	public void setBackground_genetic_input_file_pattern(
			String background_genetic_input_file_pattern) {
		this.background_genetic_input_file_pattern = background_genetic_input_file_pattern;
	}

	/**
	 * @return the storeCheckpoints
	 */
	public boolean isStoreCheckpoints() {
		return storeCheckpoints;
	}

	/**
	 * @param storeCheckpoints the storeCheckpoints to set
	 */
	public void setStoreCheckpoints(boolean storeCheckpoints) {
		this.storeCheckpoints = storeCheckpoints;
	}

	
	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "loadGenetic";
	}
	
	public void registerFileReader(AbstractGeneticFileDataReader reader, String[] extensions){
		for (int i=0; i<extensions.length; i++){
			extensionsHash.put(extensions[i].toLowerCase(), reader);
		}
	}
	public AbstractDataReader getReaderByExtension(String extension) {
		if (extensionsHash.containsKey(extension.toLowerCase()))
			return extensionsHash.get(extension.toLowerCase());
		return null;
	}
	public AbstractGeneticFileDataReader getReaderByFilename(String filename) {
		filename =  filename.toLowerCase();
		for (String extension:extensionsHash.keySet()){
			if (filename.endsWith(extension)) return extensionsHash.get(extension);
		}
		String error = "filename '" + filename + "' has a file extension that is not supported by the system";
		System.out.println(error);
		return null;
	}
	
	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config) throws ConfigurationException {
		if (config.containsKey("genetic-input-file-pattern")) {
			this.setGeneticInputFilePattern((config
					.getProperty("genetic-input-file-pattern").toString()));
		}
		
		if (config.containsKey("background-genetic-input-file-pattern")) {
			this.setBackgroundInputFilePattern((config
					.getProperty("background-genetic-input-file-pattern").toString()));
		}
		
		if (config.containsKey("storeCheckpoints")) {
			storeCheckpoints = Boolean.parseBoolean(config.getProperty("storeCheckpoints").toString());
		}
	}

	public String getGeneticInputFilePattern() {  //TODO: currently duplicated with general
		if (genetic_input_file_pattern == null) {
			System.err.println("Warning 'genetic_input_file_pattern' is null!");
		}
		return genetic_input_file_pattern;
	}

	public void setGeneticInputFilePattern(String genetic_input_file_pattern) {
		this.genetic_input_file_pattern = genetic_input_file_pattern;
	}
	

	public String getBackgroundInputFilePattern() {
		if (background_genetic_input_file_pattern == null) {
			System.err
					.println("Warning 'background_input_file_pattern' is null!");
		}
		return background_genetic_input_file_pattern;
	}

	public void setBackgroundInputFilePattern(
			String background_input_file_pattern) {
		this.background_genetic_input_file_pattern = background_input_file_pattern;
	}

}
