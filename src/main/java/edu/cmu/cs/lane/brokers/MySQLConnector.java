/**
 * 
 */
package edu.cmu.cs.lane.brokers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsMySQL;

/**
 * Provides a wrapper for MySQL capabilities
 * @author zinman
 *
 */
public class MySQLConnector {

	static private LinkedList<Connection> connectionPool;
	static private String dbUrl; 
    public MySQLConnector(){
		
    }
    static public void initialize() {
    	connectionPool = new LinkedList<Connection>();
		//setupConnection();
		 dbUrl = "jdbc:mysql://" + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBHost() + ":"
				+ ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBPort() + "/" + ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBName();
		 if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			 System.out.println("MySQLConnector instantiated with :" + dbUrl);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }

	static private Connection setupConnection() {
		try {
			Connection conn = DriverManager.getConnection(dbUrl, ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBUsername(),
					((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBPassword());
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isDebugMode())
				System.out.println("DB Connection established to "
						+ ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBHost());
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	static public Connection getConnection() {
		if (connectionPool.isEmpty()){
			return setupConnection();
		}else {
			return connectionPool.remove();
		}
	}
	static public void closeConnection(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close(); //close connection
			//add to connection pool - make sure not to keep stale connections
		}
	}
}
