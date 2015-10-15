/**
 * 
 */
package edu.cmu.cs.lane.brokers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;

/**
 * 
 * This class provides a broker for analyses created by the user and have options to store and load analyses sets
 * @author zinman
 *
 */
public class AnalysisSetBroker {
	
	/**
	 * 
	 */
	public static void initialize() {
		
	}

	
	/**
	 * Gets a list of the available models in the database. Filters options are available as a tuple of field and filter pattern  (e.g., by 
	 * algorithm type)
	 * @param options		General options
	 * @param filters 		A hash with field name to filter and pattern to filter by
	 * @param retrieveFull		and indicator whether to retrieve only the list of models (names and info) or also the model information for 
	 * all features 
	 * @return	A list of models
	 */
	static public ArrayList<AnalysisSetDetails> getAnalysesList(Hashtable<String,String> filters){
		return getAnalysesListFromDatabase(filters);
	}
	
	/**
	 * 
	 * @return table checksum 
	 */
	static public long getAnalysesListChecksum(){
		Connection conn = MySQLConnector.getConnection();
		PreparedStatement pStmt = null;
		ResultSet rs;
		long checksum = -1;
		try {	
			String query = "CHECKSUM TABLE AnalysisSet";
			pStmt = conn.prepareStatement(query);
			pStmt.execute();
			rs = pStmt.getResultSet();
			if (rs.next()) {
				checksum = rs.getLong(2);
			}
			MySQLConnector.closeConnection(conn);
		} catch (SQLException e) {
			System.err.println("SQL code does not execute.");
			e.printStackTrace();
		} finally {
			if (pStmt != null) {
				try {
					pStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pStmt = null;
			}
		}
		return checksum;
	}
	
	/**
	 * get the a list of analyses from the database 
	 * @param filters
	 * @return
	 */
	static private ArrayList<AnalysisSetDetails> getAnalysesListFromDatabase(Hashtable<String,String> filters){
		ArrayList<AnalysisSetDetails> analysisSetList = new ArrayList<AnalysisSetDetails>(); 
		Connection conn = MySQLConnector.getConnection();
		PreparedStatement pStmt = null;
		ResultSet rs;
		try {	
			String query = "select setId, analysisName, creationTime, additionalInfo from AnalysisSet";
			pStmt = conn.prepareStatement(query);
			pStmt.execute();
			rs = pStmt.getResultSet();
			while (rs.next()) {
				AnalysisSetDetails analysisSet = new AnalysisSetDetails();
				analysisSet.id = rs.getInt(1);
				analysisSet.name = rs.getString(2);
				analysisSet.creationTime = rs.getTimestamp(3);
				analysisSet.additionalInfo = rs.getString(4);
				analysisSetList.add(analysisSet);
			}
			MySQLConnector.closeConnection(conn);
		} catch (SQLException e) {
			System.err.println("SQL code does not execute.");
			e.printStackTrace();
		} finally {
			if (pStmt != null) {
				try {
					pStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pStmt = null;
			}
		}
		return analysisSetList;
	}

}
