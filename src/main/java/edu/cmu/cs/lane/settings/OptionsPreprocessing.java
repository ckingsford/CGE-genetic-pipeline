/**
 * 
 */
package edu.cmu.cs.lane.settings;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author zinman
 *
 */
public class OptionsPreprocessing extends AbstractOptions {

	String imputerName;
	String[] steps;
	int windowSize = -1;
	boolean isWeightedImputation = true;
	
	/**
	 * @return the isWeightedImputation
	 */
	public boolean isWeightedImputation() {
		return isWeightedImputation;
	}

	/**
	 * @param isWeightedImputation the isWeightedImputation to set
	 */
	public void setWeightedImputation(boolean isWeightedImputation) {
		this.isWeightedImputation = isWeightedImputation;
	}

	/**
	 * @return the windowSize
	 */
	public int getWindowSize() {
		if (windowSize == -1){
			System.out.println("ERROR: No imputation windowSize was defined in the settings.");
		}
		return windowSize;
	}

	/**
	 * @param windowSize the windowSize to set
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "preprocessing";
	}

	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config)  throws ConfigurationException {
		if (config.containsKey("steps")) {
			String stepsS = (String) config.getProperty("steps");
			stepsS.trim();
			if (!stepsS.equals(""))	steps = stepsS.split("[\\s,;]+");
		}
		if (config.containsKey("imputerName")) {imputerName = (String) config.getProperty("imputerName");}
		if (config.containsKey("windowSize")) {windowSize = Integer.parseInt((String)config.getProperty("windowSize"));}
		if (config.containsKey("weightedImputation")) {isWeightedImputation = Boolean.parseBoolean((String) config.getProperty("weightedImputation"));}

	}

	/**
	 * @return the steps
	 */
	public String[] getSteps() {
		return steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(String[] steps) {
		this.steps = steps;
	}

	/**
	 * @return the imputerName
	 */
	public String getImputerName() {
		return imputerName;
	}

	/**
	 * @param imputerName the imputerName to set
	 */
	public void setImputerName(String imputerName) {
		this.imputerName = imputerName;
	}
}
