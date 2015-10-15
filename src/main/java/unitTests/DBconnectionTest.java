/**
 * 
 */
package unitTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs.lane.annotations.AnnotatorsFactory;
import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;
import edu.cmu.cs.lane.annotations.SnpEffVariantAnnotation;
import edu.cmu.cs.lane.annotations.SnpEffFeaturesAnnotator;
import edu.cmu.cs.lane.brokers.AnalysisSetBroker;
import edu.cmu.cs.lane.brokers.CGEModelBroker;
import edu.cmu.cs.lane.brokers.MySQLConnector;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.pipeline.dataanalyzer.ExampleAnalyzer;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsMySQL;

/**
 * @author zinman
 *
 */
public class DBconnectionTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.cmu.cs.lane.brokers.CGEModelBroker#MyqlLoadModel(edu.cmu.cs.lane.settings.AbstractOptions, edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer, int)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testMyqlLoadModel() throws ConfigurationException {
		OptionsMySQL optionsMysql = new OptionsMySQL();
		optionsMysql.setDBHost("localhost");
		optionsMysql.setDBName("CGE");
		optionsMysql.setDBPort("3306");
		optionsMysql.setDBUsername("");
		optionsMysql.setDBPassword("");
		
		optionsMysql.setDBHost("cge.compbio.cs.cmu.edu");
		optionsMysql.setDBUsername("username");
		optionsMysql.setDBPassword("password");
		
		OptionsFactory.initializeEmpty(false);
		OptionsFactory.addOptions(optionsMysql);
		MySQLConnector.initialize();
		//System.out.println(AnalysisSetBroker.getAnalysesListChecksum());
		//System.out.println(AnalysisSetBroker.getAnalysesList(null).toString());
/*		ArrayList<AnalysisSetDetails> analysisList =  AnalysisSetBroker.getAnalysesList(null);
		for (int i=0; i<analysisList.size(); i++){
			System.out.println(analysisList.get(i).toString());
		}
		int analysisSetId = analysisList.get(0).id;
		ArrayList<CGEModel> models = CGEModelBroker.LoadModelMySQL(analysisSetId,true);
		for(int i=0; i< models.size(); i++){
			System.out.println(models.get(i).getDetails());
		}
		System.out.println(models.get(0).getModel().get(0));
*/		
		AnnotatorsFactory.initialize(null);
		SnpEffFeaturesAnnotator annotator = (SnpEffFeaturesAnnotator) AnnotatorsFactory.getAnnotator("snpEff");
		ArrayList<SampleGeneticFeatureBean> features = new ArrayList<SampleGeneticFeatureBean>();
		SampleGeneticFeatureBean feature = new SampleGeneticFeatureBean();
		feature.id = "22.17099107";
		features.add(feature);
		ArrayList<SampleGeneticFeatureBean> annFeatures = annotator.annotate(features);
		System.out.println(annFeatures.toString());
		
		
		
		/* test - connect to mysql manually
		try {
			String dbUrl = "jdbc:mysql://" + optionsMysql.getDBHost() + ":"
					+ optionsMysql.getDBPort() + "/" + optionsMysql.getDBName();
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbUrl, optionsMysql.getDBUsername(),
					optionsMysql.getDBPassword());
			conn.createStatement();
			System.out.println("MySQL connection established");
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
	
}
