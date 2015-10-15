package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import edu.cmu.cs.lane.brokers.store.MySQLStoreModule;
import edu.cmu.cs.lane.datatypes.dataset.DataSetType;

public class CalculatePermuteAccuracy {

    public void calculateAccuracy(File targetFile, File backgroundFile, int analysisId) {
	DataSetType targetSet = loadDataSet(targetFile);
	DataSetType backgroundSet = loadDataSet(backgroundFile);
	
	double model[] = createModel(backgroundSet, analysisId);
	int[] targetPredictions = getPredictions(model, targetSet);
	int[] backgroundPredictions = getPredictions(model, backgroundSet);
	
	double accuracy = calculateAccuracy(targetPredictions, backgroundPredictions);
	System.out.println("Accuracy = " + accuracy);
    }
    
    private double calculateAccuracy(int[] targetPredictions, int[] bgPredictions) {
	    double accuracy = 0;

	    int totalPredictions = 0;

	    if (targetPredictions != null) {
	      totalPredictions += targetPredictions.length; 
	      for (int i = 0; i < targetPredictions.length; i++) {
	        if (targetPredictions[i] == 1){
	          accuracy ++;
	        }
	      }
	    }
	    if (bgPredictions != null) {
	      totalPredictions += bgPredictions.length;
	      for (int i = 0; i < bgPredictions.length; i++) {
	        if (bgPredictions[i] == -1){
	          accuracy ++;
	        }
	      }
	    }
	    return accuracy/totalPredictions*100;
	  }
    
    private double[] createModel(DataSetType targetSet, int analysisId) {
	double[] model = new double[targetSet.getSnpIdsCount()];
	
	MySQLStoreModule db = new MySQLStoreModule();
	String query = "SELECT featureId, featureValue FROM AnalysisResults WHERE analysisId = " + analysisId;
	ResultSet rs = db.query(query);
	Map<String, Double> dbModel = new HashMap<String, Double>();
	try {
	    while (rs.next()) {
	        String featureId = rs.getString("featureId");
	        Double featureValue = rs.getDouble("featureValue");
	        dbModel.put(featureId, featureValue);
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		if (rs != null) {
		    rs.close();
		}
		rs = null;
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	
	
	for (int i = 0; i < targetSet.getSnpIdsCount(); i++) {
	    String snpId = targetSet.getSnpId(i);
	    Double weight = dbModel.get(snpId);
	    if (weight != null) {
		model[i] = weight.doubleValue();
	    }
	    else {
		model[i] = 0;
	    }
	}
	return model;
    }
    
    private DataSetType loadDataSet(File inFile) { 
	int lineNum = getNumberOfLinesInFile(inFile);
	int numberOfSnps = lineNum-1;
	DataSetType dataset = null;
	BufferedReader bIn = null;
	try {
	    bIn = new BufferedReader(new FileReader(inFile));
	    String str;
	    String[] tokens;
	    str = bIn.readLine(); //reading header line
	    tokens = str.split("\\t"); 
	    int numberOfPatients = tokens.length -1;

	    dataset = new DataSetType(numberOfSnps, numberOfPatients);
	    for (int p =1; p<tokens.length; p++){
		dataset.setPatientId(p-1, tokens[p]);
	    }

	    int curSnp = 0;
	    int curVal;

	    while ((str = bIn.readLine()) != null) {
		tokens = str.split("\\t");
		dataset.setSnpId(curSnp, tokens[0]);
		for (int i=1; i<tokens.length;i++){
		    curVal = Integer.parseInt(tokens[i]);
		    dataset.setData(curSnp, i-1, curVal);
		    if (curVal!=0){		
			dataset.setNzElement(i-1, dataset.getNzElement(i-1) + 1);
			dataset.setTotalNzElementsCount(dataset.getTotalNzElementsCount() + 1);
		    }
		}
		curSnp++;
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (bIn != null) {
		try {
		    bIn.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		bIn = null;
	    }
	}
	return dataset;
    }

    private int getNumberOfLinesInFile(File file){
	int count = 0;
	LineNumberReader lnr = null;
	try {
	    lnr = new LineNumberReader(new FileReader(file));
	    lnr.skip(Long.MAX_VALUE); //should work for files less than 16 Exabytes
	    count = lnr.getLineNumber();  //assuing a \n on the last line
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (lnr != null) {
		try {
		    lnr.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		lnr = null;
	    }
	}

	return count;
    }

    private int[] getPredictions(double [] model, DataSetType dataset){
	int[] predictions = new int [dataset.getPatientIdsCount()];

	//make predictions
	for (int i = 0; i < dataset.getPatientIdsCount(); i++) {
	    double patientPrediction = 0;
	    for (int g = 0; g < model.length; g++) {
		patientPrediction += model[g] * dataset.getData(g, i);
	    }
	    if (patientPrediction > 0){predictions[i] = 1;} 
	    else {predictions[i] = -1;}
	}
	return predictions;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	if (args.length < 3) {
	    System.err.println("Not enough arguments");
	}
	else {
	    String targetFilename = args[0];
	    File targetFile = new File(targetFilename);
	    
	    String backgroundFilename = args[1];
	    File backgroundFile = new File(backgroundFilename);
	    
	    int analysisId = Integer.parseInt(args[2]);
	    CalculatePermuteAccuracy calc = new CalculatePermuteAccuracy();
	    calc.calculateAccuracy(targetFile, backgroundFile, analysisId);
	}

    }

}
