/**
 * 
 */
package edu.cmu.cs.lane.annotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.cmu.cs.lane.brokers.MySQLConnector;

/**
 * 
 * Provides a wrapper for SnpEff type of annotator
 * @author zinman
 *
 */
public class SnpEffFeaturesAnnotator extends AbstractFeaturesAnnotator {

	/**
	 * @see edu.cmu.cs.lane.annotations.AbstractFeaturesAnnotator#getName()
	 */
	@Override
	public String getName() {
		return "snpEff";
	}
	
	/**
	 * returns a list of annotations for each featureId that was provided 
	 * @see edu.cmu.cs.lane.annotations.AbstractFeaturesAnnotator#annotate(java.util.ArrayList)
	 */
	@Override 
	public ArrayList<SampleGeneticFeatureBean> annotate(ArrayList<SampleGeneticFeatureBean> featuresInput) {
		Connection conn = MySQLConnector.getConnection();
		try {
			String sqlquery = "SELECT distinct geneName, rsId, chromosomeNo, snpPosition, transcript, effect, impact, functionalClass, coding, codonChange, aminoAcidChange,exon "
		    + "FROM SnpEffInfo "
		    + "WHERE CONCAT(chromosomeNo,'.',snpPosition) = ?"
		    + " ORDER BY CASE impact"
	        + " WHEN 'HIGH' THEN 0"
	        + " WHEN 'MODERATE' THEN 2"
	        + " WHEN 'LOW' THEN 3"
	        + " ELSE 99 "
		    + " END";
			

			
			PreparedStatement pStmt = conn.prepareStatement(sqlquery);
			for (int i=0; i<featuresInput.size(); i++){
				String featureId = featuresInput.get(i).id;
				pStmt.setString(1, featureId);
				ResultSet rs = pStmt.executeQuery();
				
				boolean variantAnnotationAdded = false;
				while (rs.next()){
					if (!variantAnnotationAdded){
						SnpEffVariantAnnotation annotation = new SnpEffVariantAnnotation();
						annotation.geneName= rs.getString("geneName");
						annotation.rsId = rs.getString("rsId");
						annotation.chr = rs.getString("chromosomeNo");
						annotation.pos = rs.getString("snpPosition");
						featuresInput.get(i).addVariantAnnotation(annotation);
						variantAnnotationAdded = true;
					}
					
					SnpEffEffectAnnotation effectAnnotation = new SnpEffEffectAnnotation();
					effectAnnotation.transcript = rs.getString("transcript");
					effectAnnotation.effect = rs.getString("effect");
					effectAnnotation.impact = rs.getString("impact");
					effectAnnotation.functionalClass = rs.getString("functionalClass");
					effectAnnotation.coding = rs.getString("coding");
					effectAnnotation.codonChange = rs.getString("codonChange");
					effectAnnotation.aminoAcidChange = rs.getString("aminoAcidChange");
					effectAnnotation.exon = rs.getInt("exon");
					
					featuresInput.get(i).addEffectAnnotation(effectAnnotation);
				}
			}
			pStmt.close();
			MySQLConnector.closeConnection(conn);
		} catch (SQLException e) {
			System.err.println("SQL code does not execute.");
			e.printStackTrace();
		} finally {
		}
		return featuresInput;

	}

	

}
