package edu.cmu.cs.lane.settings;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public abstract class AbstractOptions {
	abstract public void readParams(PropertiesConfiguration config) throws ConfigurationException;
	
	

	/**
	 * @return
	 */
	public abstract String getName();
}
