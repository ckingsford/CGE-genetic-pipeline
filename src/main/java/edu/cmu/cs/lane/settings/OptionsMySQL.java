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
public class OptionsMySQL extends AbstractOptions{
	/**
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "mysql";
	}
	
	// DB
	private String db_username;
	private String db_password;
	private String db_name;
	private String db_host;
	private String db_port;
	
	public String getDBPassword() {
		if (db_password == null) {
			System.err.println("Warning 'db_password' is null!");
		}
		return db_password;
	}

	public void setDBPassword(String db_password) {
		this.db_password = db_password;
	}

	public String getDBUsername() {
		if (db_username == null) {
			System.err.println("Warning 'db_username' is null!");
		}
		return db_username;
	}

	public void setDBUsername(String db_username) {
		this.db_username = db_username;
	}

	public String getDBName() {
		if (db_name == null) {
			System.err.println("Warning 'db_name' is null!");
		}
		return db_name;
	}

	public void setDBName(String db_name) {
		this.db_name = db_name;
	}

	public String getDBHost() {
		if (db_host == null) {
			System.err.println("Warning 'db_host' is null!");
		}
		return db_host;
	}

	public void setDBHost(String db_host) {
		this.db_host = db_host;
	}

	public String getDBPort() {
		if (db_port == null) {
			System.err.println("Warning 'db_port' is null!");
		}
		return db_port;
	}

	public void setDBPort(String db_port) {
		this.db_port = db_port;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#initialize(java.lang.String)
	 */
	@Override
	public void readParams(PropertiesConfiguration config) throws ConfigurationException {
		if (config.containsKey("db-username")) {
			this.setDBUsername((config.getProperty("db-username").toString()));
		}
		if (config.containsKey("db-password")) {
			this.setDBPassword((config.getProperty("db-password").toString()));
		}
		if (config.containsKey("db-name")) {
			this.setDBName((config.getProperty("db-name").toString()));
		}
		if (config.containsKey("db-host")) {
			this.setDBHost((config.getProperty("db-host").toString()));
		}
		if (config.containsKey("db-port")) {
			this.setDBPort((config.getProperty("db-port").toString()));
		}
		
	}



}
