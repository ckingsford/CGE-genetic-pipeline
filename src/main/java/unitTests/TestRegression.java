/**
 * 
 */
package unitTests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import edu.cmu.cs.lane.brokers.InitializeBrokers;
import edu.cmu.cs.lane.pipeline.dataanalyzer.DataAnalysisPhaseController;
import edu.cmu.cs.lane.pipeline.pipelineConfig.PhaseController;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * @author zinman
 *
 */
public class TestRegression {

	static public void main (String[] args) throws ConfigurationException, IOException{
		System.out.println("Testing regression");
		if (args.length > 0) {	
			OptionsFactory.initialize(args[0], null);
			
		} else {
			System.out.println("Please provide properties file as a parameter.");
			System.exit(1);
		}
		InitializeBrokers.initializeStaticObjects();

		PhaseController controller = new DataAnalysisPhaseController();
		controller.runPhase();
		
		//compare outputs - check that there are the same features
		String newInput = "./samples/regression/output/test-regression-fixed_10.000_1.000_results.txt";
		FileInputStream fin =  new FileInputStream(newInput);
		BufferedReader myInput = new BufferedReader (new InputStreamReader(fin));
		String line;
		StringBuilder newPred = new StringBuilder();
		while (( line = myInput.readLine()) != null) {  
			newPred.append(line.split("\t")[0]);	
		}
		myInput.close();
		
		
		String fixedInput = "./samples/regression/testRegressionOutput/test-regression-fixed_10.000_1.000_results.txt";
		fin =  new FileInputStream(fixedInput);
		myInput = new BufferedReader (new InputStreamReader(fin));
		StringBuilder fixedPred = new StringBuilder();
		while (( line = myInput.readLine()) != null) {  
			fixedPred.append(line.split("\t")[0]);	
		}
		myInput.close();
		
		if(fixedPred.toString().equals(newPred.toString())){
			System.out.println("#############  TEST REGRESSION SUCCEEDED #############");
		}else{
			System.out.println("#############  TEST REGRESSION FAILED #############");
			System.out.println("NEW  : " + newPred.toString());
			System.out.println("FIXED: " + fixedPred.toString());
		}
	}

}
