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
public class OptionsLD extends AbstractOptions {

	String inputLocation = "";
	String inputPattern = "";
	Double threshold = 0.0;
	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "LD";
	}
	
	/**
	 * 
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config) {
		
		if (config.containsKey("LDinputLocation")) {inputLocation = (String) config.getProperty("LDinputLocation");}
		if (config.containsKey("LDinputPattern")) {inputPattern = (String) config.getProperty("LDinputPattern");}
		if (config.containsKey("LDthreshold")) {threshold = (Double) config.getDouble("LDthreshold",0.75);}
	}
	
	/**
	 * @return the inputLocation
	 */
	public String getInputLocation() {
		if (inputLocation.equals("")){
			System.out.println("Warning: no LD input location was provided");
		}
		return inputLocation;
	}

	/**
	 * @param inputLocation the inputLocation to set
	 */
	public void setInputLocation(String inputLocation) {
		this.inputLocation = inputLocation;
	}

	/**
	 * @return the inputPattern
	 */
	public String getInputPattern() {
		if (inputPattern.equals("")){
			System.out.println("Warning: no LD input pattern was provided");
		}

		return inputPattern;
	}

	/**
	 * @param inputPattern the inputPattern to set
	 */
	public void setInputPattern(String inputPattern) {
		this.inputPattern = inputPattern;
	}

	/**
	 * @return the threshold
	 */
	public Double getThreshold() {
		if (inputLocation.equals("")){
			System.out.println("Warning: no LD thershold was provided");
		}

		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}




	
}
