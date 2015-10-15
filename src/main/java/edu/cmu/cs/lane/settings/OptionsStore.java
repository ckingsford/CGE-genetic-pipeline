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
public class OptionsStore extends AbstractOptions {

	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "store";
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config)
			throws ConfigurationException {
	}

	

}
