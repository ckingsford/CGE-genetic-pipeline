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

import edu.cmu.cs.lane.brokers.store.CGEStoreCenter;
import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.model.CGEModelFeatureBean;
import edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer;

/**
 * The CGEModelBroker class handles storing and loading models from the database
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 */
public class CGEModelBroker {
	
	/**
	 * initialize the broker using the parameters provided in options (e.g., Mysql credentials)
	 * @param options
	 */
	static public void initialize(){
		
	}

	/**
	 * Stores a model  
	 * @param options
	 * @param model
	 */
	static public void StoreModel(ArrayList<CGEModel> model){
		for (int i=0; i<model.size(); i++) {
			CGEStoreCenter.storeModel(model.get(i)); 
		}
	}

	/**
	 * remove model from the database
	 * @param analysisSetId
	 * @return
	 */
	static public boolean RemoveModel(int analysisSetId){
		Connection conn = MySQLConnector.getConnection();
		PreparedStatement pStmt = null;
		ResultSet rs;
		ArrayList<Integer> analysisIds = new ArrayList<Integer>();
		try {	
			String query = "SELECT analysisId FROM AnalysisDetails WHERE analysisSetId = " + analysisSetId;
			pStmt = conn.prepareStatement(query);
			pStmt.execute();
			rs = pStmt.getResultSet();
			while (rs.next()) {
				analysisIds.add(rs.getInt(1));
			}

			//deleting from the results table
			query = "DELETE FROM AnalysisResults WHERE analysisId = ?";
			pStmt = conn.prepareStatement(query);
			for (Integer analysisId : analysisIds){
				pStmt.setInt(1, analysisId);
				pStmt.addBatch();
			}
			pStmt.executeBatch();

			//deleting from the details table
			query = "DELETE FROM AnalysisDetails WHERE analysisSetId = " + analysisSetId;
			pStmt = conn.prepareStatement(query);
			pStmt.execute();
			
			//deleting from the set table
			query = "DELETE FROM AnalysisSet WHERE setId = " + analysisSetId;
			pStmt = conn.prepareStatement(query);
			pStmt.execute();

			
			
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
		return true;
	}
	
	
	
	
	/**
	 * Loads a model from the database
	 * @param options
	 * @return a model that was loaded
	 */
	static public ArrayList<CGEModel> LoadModelMySQL(int analysisSetId,boolean retrieveFull) { 
		ArrayList<CGEModel> modelList = new ArrayList<CGEModel>(); 
		Connection conn = MySQLConnector.getConnection();
		PreparedStatement pStmt = null;
		ResultSet rs;
		try {	
			String query = "SELECT analysisId, algorithm, algParameters, additionalInfo FROM AnalysisDetails WHERE analysisSetId = " + analysisSetId;
			pStmt = conn.prepareStatement(query);
			pStmt.execute();
			rs = pStmt.getResultSet();
			while (rs.next()) {
				CGEModel model = new CGEModel();
				model.details.id = rs.getInt(1);
				model.details.algorithmName = rs.getString(2);
				model.details.algParameters = rs.getString(3);
				model.details.additionalInfo = rs.getString(4);
				modelList.add(model);
			}
			
			if (retrieveFull){
				query = "select featureId, featureType, responseVariable, featureValue from AnalysisResults where analysisId = ?";
				pStmt = conn.prepareStatement(query);
				
				for (int i=0; i< modelList.size(); i++){
					pStmt.setInt(1, modelList.get(i).details.id);
					pStmt.execute();
					rs = pStmt.getResultSet();
				
					while (rs.next()) {
						CGEModelFeatureBean feature = new CGEModelFeatureBean();
						feature.id = rs.getString(1);
						feature.type = rs.getString(2);
						feature.var = rs.getString(3);
						feature.val = rs.getDouble(4);
						modelList.get(i).add(feature);
					}
				}
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

		return modelList;
	}
	
}
