/**
 * 
 */
package unitTests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.jblas.DoubleMatrix;
import org.junit.Test;

import edu.cmu.cs.lane.annotations.AnnotatorsFactory;
import edu.cmu.cs.lane.annotations.SampleGeneticFeatureBean;
import edu.cmu.cs.lane.annotations.SnpEffFeaturesAnnotator;
import edu.cmu.cs.lane.brokers.CGEModelBroker;
import edu.cmu.cs.lane.brokers.InitializeBrokers;
import edu.cmu.cs.lane.brokers.MySQLConnector;
import edu.cmu.cs.lane.brokers.load.CGELoadGeneticCenter;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.prediction.CGEPrediction;
import edu.cmu.cs.lane.matrixmath.MatrixUtils;
import edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer;
import edu.cmu.cs.lane.pipeline.dataanalyzer.DataAnalyzersFactory;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.filters.LookUpDataFilter;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;
import edu.cmu.cs.lane.settings.OptionsMySQL;

/**
 * @author zinman
 *
 */
public class PredictionTest {

	@Test
	public void test() {
		OptionsFactory.initializeEmpty(true);

		OptionsMySQL mockOptions  = new OptionsMySQL();
		mockOptions.setDBHost("cge.compbio.cs.cmu.edu");
		mockOptions.setDBName("CGE");
		mockOptions.setDBPort("3306");
		mockOptions.setDBUsername("username");
		mockOptions.setDBPassword("password");
		OptionsFactory.addOptions(mockOptions);
		MySQLConnector.initialize();

		OptionsLoadGenetic optGenetics = new OptionsLoadGenetic();
		optGenetics.setGeneticInputFilePattern("chr([\\d]+)\\.tab.txt");
		OptionsFactory.addOptions(optGenetics);
		
		OptionsGeneral optGeneral = new OptionsGeneral();
		optGeneral.setWorkingFolder("../samples/classification/working/");
		optGeneral.setLoadGeneticSource("file");
		optGeneral.setGeneticInputOrientation("columns-as-patients");
		OptionsFactory.addOptions(optGeneral); //override previous general	
		
		InitializeBrokers.initializeStaticObjects();
		DataAnalyzersFactory.initialize();

		//loadModel
		String model_id_s = "24";
		System.out.println("Attempting to load model:");
		ArrayList<CGEModel> model = CGEModelBroker.LoadModelMySQL(Integer.parseInt(model_id_s), true);
		System.out.println("model loaded " + model.get(0).details.toString());
		
	    //load patients
		ArrayList<AbstractDataFilter> dataFilters = new ArrayList<AbstractDataFilter>(1);
    	LookUpDataFilter lookupDataFilter = new LookUpDataFilter(); //Data filter -  based on the features in the model
    	for (int m = 0; m < model.size(); m++){
	    	ArrayList<String> ids = new ArrayList<String>(model.get(m).size());
	    	for (int i=0; i<model.get(m).getFeatures().size();i++){
	    		ids.add(model.get(m).getFeatures().get(i).id);
	    	}
	    	lookupDataFilter.populateHashByPosition(ids);
	    	//lookupDataFilter.populateHashByRsID(ids);
    	}
    	dataFilters.add(lookupDataFilter);
		
    	ArrayList<SamplesGeneticData> patientGeneticInfo = CGELoadGeneticCenter.loadNext(dataFilters); //return value should potentially be an array with only one element corresponding to one patient
    	
    	System.out.println("patients count: " +patientGeneticInfo.get(0).getSamplesCount() + "\t features count: " + patientGeneticInfo.get(0).getFeaturesCount());
    	
		for(int i=0; i< model.size(); i++){
	    	String usedAnalyzer = model.get(i).details.algorithmName;
	    	AbstractAnalyzer analyzer = DataAnalyzersFactory.create(usedAnalyzer);
	    	SamplesDataset samplesDataset = new SamplesDataset();
	    	samplesDataset.setGeneticData(patientGeneticInfo.get(0));
			ArrayList<CGEPrediction> predictions = analyzer.applyModel(model.get(i), samplesDataset);
			System.out.println("prediction for the first patient: " + predictions.get(0).getPredctions().get(0).toString());
		}

		AnnotatorsFactory.initialize(null);
		SnpEffFeaturesAnnotator annotator = (SnpEffFeaturesAnnotator) AnnotatorsFactory.getAnnotator("snpEff");
		ArrayList<SampleGeneticFeatureBean> features = new ArrayList<SampleGeneticFeatureBean>();
		
        for (int i=0; i<patientGeneticInfo.get(0).getFeaturesCount();i++){        	
        	if (patientGeneticInfo.get(0).getData(0,i) == 1 || patientGeneticInfo.get(0).getData(0,i) == 2){//presumably only one patient
        		SampleGeneticFeatureBean feature = new SampleGeneticFeatureBean();
        		feature.id = patientGeneticInfo.get(0).getFeatureName(i);
        		feature.rsId = patientGeneticInfo.get(0).getExtendedFeatureInfo(i);
        		feature.value = (Byte) (byte) patientGeneticInfo.get(0).getData(0,i); //presumably only one patient
        		features.add(feature);
        	}
        }
		
		features = annotator.annotate(features);

    	int a = 3;
		a++;


	}

}
