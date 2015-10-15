package edu.cmu.cs.lane.brokers.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.cmu.cs.lane.brokers.MySQLConnector;
import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsMySQL;

public class MySQLStoreModule extends AbstractStoreModule {


    /**
	 * @see edu.cmu.cs.lane.brokers.store.AbstractStoreModule#getName()
	 */
	@Override
	public String getName() {
		return "mysql";
	}
	
	public MySQLStoreModule() {

	}




	
	/** 
	 * @see edu.cmu.cs.lane.brokers.store.AbstractStoreModule#store(edu.cmu.cs.lane.datatypes.model.CGEModel, edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer)
	 */
	@Override
	public int storeModel(CGEModel model) {
		Connection conn = MySQLConnector.getConnection();

		int analysisId = -1;
		PreparedStatement pStmt = null;
		ResultSet generatedKeys = null;
		try {		
			String insertInto = "INSERT INTO AnalysisDetails "
					+ "(analysisId, algorithm, algParameters, cvPercent, avgAccuracy, additionalInfo, patients, targetSource, backgroundSource) "
					+ "VALUES (NULL,?,?,?,?,?,?,?,?)";
			pStmt = conn.prepareStatement(insertInto, Statement.RETURN_GENERATED_KEYS);
			pStmt.setString(1, model.getDetails().algorithmName);
			pStmt.setString(2, model.getDetails().algParameters);
			pStmt.setDouble(3, model.getDetails().cvPercent);
			pStmt.setDouble(4, model.getDetails().avgAccuracy);
			pStmt.setString(5, model.getDetails().additionalInfo);
			pStmt.setString(6, model.getDetails().patients);
			pStmt.setString(7, model.getDetails().targetSource);
			pStmt.setString(8, model.getDetails().backgroundSource);
			pStmt.executeUpdate();

			generatedKeys = pStmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				analysisId = generatedKeys.getInt(1);
			}
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
		
		//store CGEModel into the database
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tStoring results to database: "
					+ ((OptionsMySQL) OptionsFactory.getOptions("mysql")).getDBName());

		pStmt = null;
		String insertStmt = null;
		try {
			String insertInto = "INSERT INTO AnalysisResults (featureId, featureType, responseVariable, featureValue, analysisId) VALUES (?, ?, ?,?, ?)";
			pStmt = conn.prepareStatement(insertInto);
			for (int i = 0; i < model.size(); ++i) {	
				pStmt.setString(1, model.getFeatures().get(i).id);
				pStmt.setString(2, model.getFeatures().get(i).type);
				pStmt.setString(3, model.getFeatures().get(i).var);
				pStmt.setDouble(4, model.getFeatures().get(i).val);
				pStmt.setInt(5, analysisId);
				pStmt.addBatch();
			}
			pStmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("SQL code does not execute: "
					+ insertStmt);
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

		//closing connection
		try {
			MySQLConnector.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return analysisId;
	}
	
	
	/**
	 * @see edu.cmu.cs.lane.brokers.store.AbstractStoreModule#storeSet(java.util.ArrayList)
	 */
	@Override
	synchronized public int storeSet(ArrayList<Integer> analysesIds, AnalysisSetDetails analysisSetDetails) {
		Connection conn = MySQLConnector.getConnection();
		//this is a synchronized process, but will not help if multiple machine accessing the same server at the same time
		int setId = -1;
		PreparedStatement pStmt = null;
		try {	
			String insertInto = "INSERT INTO AnalysisSet(setId, analysisName, additionalInfo) VALUES (NULL,?,?)";
			pStmt = conn.prepareStatement(insertInto,Statement.RETURN_GENERATED_KEYS);
			pStmt.setString(1, analysisSetDetails.name);
			pStmt.setString(2, analysisSetDetails.additionalInfo);
			pStmt.executeUpdate();
			
			
			ResultSet generatedKeys = pStmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				setId = generatedKeys.getInt(1);
			}
		
			String updateQuery = "UPDATE AnalysisDetails  SET analysisSetId = ? WHERE analysisId = ?";
			pStmt = conn.prepareStatement(updateQuery);
			for (int i=0; i<analysesIds.size(); i++){
				pStmt.setInt(1, setId);
				pStmt.setInt(2, analysesIds.get(i));
				pStmt.addBatch();
			}
			pStmt.executeBatch();

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
		return setId;
	}
	
	
	
	
	

	public void makeGeneInfoFile(String fileName) throws Exception {
	}

	public void makeDiseaseInfoFile(String fileName) throws Exception {
	}

	public void makeDrugInfoFile(String fileName) throws Exception {
	}

	public void insertIntoMutationGeneTable(String fileName) throws Exception {
	}



	public long getGeneId(long snpPosition) throws Exception {

		Statement st = null;
		ResultSet res = null;
		long geneId = -1;
		Connection conn = MySQLConnector.getConnection();
		try {
			st = conn.createStatement();
			res = st.executeQuery("SELECT geneId FROM MutationGene where position="
					+ snpPosition);

			while (res.next()) {
				geneId = Long.parseLong(res.getString("geneId"));
				// System.out.println(snpPosition + "-->" + geneId);
			}

		} catch (SQLException s) {
			System.err.println("SQL code does not execute.");
		} finally {
			if (res != null) {
				res.close();
				res = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
		MySQLConnector.closeConnection(conn);
		return geneId;
	}

	public ArrayList<Long> getGeneId(String snpId) throws Exception {

		Statement st = null;
		ResultSet res = null;
		long geneId = -1;
		ArrayList<Long> geneIds = new ArrayList<Long>();
		Connection conn = MySQLConnector.getConnection();
		try {
			st = conn.createStatement();
			res = st.executeQuery("SELECT geneId FROM MutationGene where rsId="
					+ snpId);

			while (res.next()) {
				geneId = Long.parseLong(res.getString("geneId"));
				// System.out.println(snpPosition + "-->" + geneId);
				if (geneId != -1) {
					geneIds.add(geneId);
				}
			}

		} catch (SQLException s) {
			System.err.println("SQL code does not execute.");
		} finally {
			if (res != null) {
				res.close();
				res = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
		MySQLConnector.closeConnection(conn);
		return geneIds;
	}


	public HashMap<String, String> getGeneDBInfo(
			HashMap<String, String> whereMap, String selectedFields)
			throws Exception {

		Statement st = null;
		ResultSet res = null;
		HashMap<String, String> resultsMap = new HashMap<String, String>();
		Iterator<String> it = whereMap.keySet().iterator();
		String whereClause = "";
		while (it.hasNext()) {
			String key = it.next();
			whereClause += key + "=" + whereMap.get(key) + " AND ";
		}
		whereClause = whereClause.trim().replaceAll("AND$", "").trim();
		String columns[] = selectedFields.split("[,]");
		String sql = "SELECT " + selectedFields + " FROM GeneInfo where "
				+ whereClause;
		Connection conn = MySQLConnector.getConnection();
		try {	
			st = conn.createStatement();
			res = st.executeQuery(sql);

			while (res.next()) {
				// geneId = Long.parseLong(res.getString("geneId"));
				// System.out.println(snpPosition + "-->" + geneId);
				for (int i = 0; i < columns.length; i++) {
					String column = columns[i];
					String value = res.getString(column);
					resultsMap.put(column, value);
				}
			}

		} catch (SQLException s) {
			System.err.println(sql + "\tSQL code does not execute. " + s);
			s.printStackTrace();
		} catch (Exception e) {
			System.err.println(sql + "\tSQL code does not execute. " + e);
			e.printStackTrace();

		} finally {
			if (res != null) {
				res.close();
				res = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
		MySQLConnector.closeConnection(conn);
		return resultsMap;
	}

	public HashMap<String, String> updateGeneDBInfo(
			HashMap<String, String> updateMap, HashMap<String, String> whereMap)
			throws Exception {

		// /TO DO change updateGeneInfo and insertGeneInfo
		Statement st = null;
		int res = -1;
		HashMap<String, String> resultsMap = new HashMap<String, String>();
		Iterator<String> it = whereMap.keySet().iterator();
		String whereClause = "";
		while (it.hasNext()) {
			String key = it.next();
			whereClause += key + "=" + whereMap.get(key) + " AND ";
		}
		whereClause = whereClause.trim().replaceAll("AND$", "").trim();
		it = updateMap.keySet().iterator();
		String updateClause = "";
		while (it.hasNext()) {
			String key = it.next();
			updateClause += key + "=" + updateMap.get(key) + ",";
		}
		updateClause = updateClause.replaceAll("[,]$", "").trim();
		String sql = "UPDATE GeneInfo set " + updateClause + " where "
				+ whereClause;
		Connection conn = MySQLConnector.getConnection();
		try {
			st = conn.createStatement();

			res = st.executeUpdate(sql);
			if (res == 0) {
				System.out.println("nothing to update for " + whereClause);
			}

		} catch (SQLException s) {
			System.out.println("SQL code does not execute. " + sql);
			s.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error At " + sql);
			e.printStackTrace();
		} finally {
			if (st != null) {
				st.close();
				st = null;
			}
		}
		MySQLConnector.closeConnection(conn);
		return resultsMap;
	}

	public void insertGeneDBInfo(HashMap<String, Object> insertMap)
			throws Exception {

		Statement st = null;
		int res = -1;
		Iterator<String> it = insertMap.keySet().iterator();
		String columns = "(";
		String values = "(";
		while (it.hasNext()) {
			String key = it.next();
			Object value = insertMap.get(key);
			columns += key + ",";
			if (value instanceof String) {
				String val = value.toString().replace("'", "\\'").trim();
				values += "'" + val + "',";
			} else {
				values += value.toString() + ",";
			}
		}

		columns = columns.replaceAll("[,]$", "").trim() + ")";
		values = values.replaceAll("[,]$", "").trim() + ")";
		String sql = "INSERT INTO GeneInfo " + columns + " values " + values;
		System.out.println(sql);
		Connection conn = MySQLConnector.getConnection();
		try {
			
			st = conn.createStatement();

			res = st.executeUpdate(sql);

			if (res == 0) {
				System.out.println("nothing to update for " + columns + "\t"
						+ values);
			}

		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println("SQL code does not execute.");
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (st != null) {
				st.close();
				st = null;
			}
			MySQLConnector.closeConnection(conn);
		}

	}

	public long getGeneIdFromPA(String genePAId) throws Exception {

		Statement st = null;
		ResultSet res = null;
		long geneId = -1;
		Connection conn = MySQLConnector.getConnection();
		try {
			st = conn.createStatement();
			res = st.executeQuery("select geneId from GeneInfo where MATCH (externalIds) AGAINST ('"
					+ genePAId + "')");

			if (res.next()) {
				geneId = Long.parseLong(res.getString("geneId"));
				// System.out.println(snpPosition + "-->" + geneId);
			}

		} catch (SQLException s) {
			System.out.println("SQL code does not execute.");
		} finally {
			if (res != null) {
				res.close();
				res = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
		MySQLConnector.closeConnection(conn);
		return geneId;
	}

	public long getLocationId(long geneId) throws Exception {

		Statement st = null;
		ResultSet res = null;
		long locationId = -1;
		Connection conn = MySQLConnector.getConnection();
		try {
			st = conn.createStatement();
			res = st.executeQuery("SELECT locationId FROM GeneLocationInfo where geneId="
					+ geneId);

			while (res.next()) {
				locationId = Long.parseLong(res.getString("locationId"));
				// System.out.println(snpPosition + "-->" + geneId);
			}

		} catch (SQLException s) {
			System.out.println("SQL code does not execute.");
		} finally {
			if (res != null) {
				res.close();
				res = null;
			}
			if (st != null) {
				st.close();
				st = null;
			}
		}
		MySQLConnector.closeConnection(conn);
		return locationId;
	}

	public String getPhenotypeId(long geneId) throws Exception {

		Statement st = null;
		ResultSet res = null;
		String phenotypeIds = "";
		Connection conn = MySQLConnector.getConnection();
		try {
			st = conn.createStatement();
			res = st.executeQuery("SELECT phenotypeId FROM PhenotypeInfo where geneId="
					+ geneId);
			int count = 0;
			while (res.next()) {
				phenotypeIds += res.getString("phenotypeId") + ",";
				// System.out.println(snpPosition + "-->" + geneId);
				count++;
			}
			if (count > 1) {
				System.out.println("multiple values found: " + phenotypeIds);
			}
		} catch (SQLException s) {
			s.printStackTrace();
			System.out.println("SQL code does not execute.");
		} finally {
			if (res != null) {
				res.close();
				res = null;
			}
			if (st != null) {
				st.close();
				st = null;
				
			}
		}
		phenotypeIds = phenotypeIds.replaceAll("[,]$", "").trim();
		MySQLConnector.closeConnection(conn);
		return phenotypeIds;
	}

	public ResultSet query(String query) {

		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = MySQLConnector.getConnection();
		try {
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		} finally {
			try {
				MySQLConnector.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rs;
	}
}
