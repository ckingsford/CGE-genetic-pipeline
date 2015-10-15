package edu.cmu.cs.lane.pipeline.dataanalyzer.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import edu.cmu.cs.lane.datatypes.dataset.DataSetType;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.datatypes.shotgun.FeatureMatrix;

public class MatrixMarketUtil {
	
	/**
	 * 
	 * @param samples
	 * @param cvIndices 
	 * @return
	 */
	public static FeatureMatrix createFeatureMatrix(ArrayList<SamplesGeneticData> samples){ //for training data
		
		ArrayList<HashSet<Integer>> rowsHashSet = null;
		
		int nzElements = 0;
		int numberOfSamples = 0;
		for (int dataset_i=0; dataset_i < samples.size(); dataset_i++){
			SamplesGeneticData dataset = samples.get(dataset_i);
			for(int r =0; r< dataset.getSamplesCount(); r++){
				if (rowsHashSet == null || rowsHashSet.get(dataset_i).contains(r)){  //use all or skip rows that are not in the training set
					for(int c =0; c< dataset.getFeaturesCount(); c++){
						if (dataset.getData(r, c)!=0.0) nzElements++;
					}
				}
			}
			numberOfSamples += dataset.getSamplesCount();
			
		}
		int currentPatient = 1; //Cumulative between datasets 
		int MMrow = 0;
		FeatureMatrix MM = new FeatureMatrix(nzElements, 3);
		MM.setRowCount(numberOfSamples);
		MM.setColumnCount(samples.get(0).getFeaturesCount()); //assumption: two datasets have the same number of features
		for (int dataset_i=0; dataset_i < samples.size(); dataset_i++){
			SamplesGeneticData dataset = samples.get(dataset_i);
			for(int r =0; r< dataset.getSamplesCount(); r++){
				if (rowsHashSet == null || rowsHashSet.get(dataset_i).contains(r)){  //use all or skip rows that are not in the training set
					for(int c =0; c< dataset.getFeaturesCount(); c++){
						if (dataset.getData(r, c)!=0.0) {
							MM.setFeature(MMrow, 0, currentPatient);//1-index-based
							MM.setFeature(MMrow, 1, c+1); //current SNP, 1-index-based
							MM.setFeature(MMrow, 2, dataset.getData(r, c)); 
							MMrow++;
						}
					}
					currentPatient++;
				}
			}
		}
		
		return MM;
	}
	
	/**
	 * 
	 * @param labels
	 * @return
	 */
	public static double[] createBinaryLabelsVector(ArrayList<SamplesLabels> labels) {
		ArrayList<HashSet<Integer>> rowsHashSet = null;
		
		int numberOfSamples=0;
		for (int dataset_i=0; dataset_i < labels.size(); dataset_i++){			
			numberOfSamples += labels.get(dataset_i).getSamplesCount();
		}
		double[] y = new double[numberOfSamples];
		int cummulativeR = 0;
		for (int dataset_i=0; dataset_i < labels.size(); dataset_i++){
			for (int r=0; r< labels.get(dataset_i).getSamplesCount(); r++){
				if (rowsHashSet == null || rowsHashSet.get(dataset_i).contains(r)){ //use all or skip rows that are not in the training set
					y[cummulativeR] = labels.get(dataset_i).getData(r, 0);	//assumption: only one column - "has condition"		
					cummulativeR++;
				}
			}
		}		
		return y;
	}
		

	/**
	 * Creates a feature matrx
	 * 
	 * @param targetData
	 * @param bgData
	 * @param exclusionListTarget
	 * @param exclusionListBg
	 * @return FeatureMatrix
	 */
	public static FeatureMatrix createFeatureMatrix(DataSetType targetData,
			DataSetType bgData,
			Hashtable<Integer, Boolean> exclusionListTarget,
			Hashtable<Integer, Boolean> exclusionListBg) {
		if (targetData == null || bgData == null) {
			return null;
		}
		int targetPatientCount = targetData.getPatientIdsCount();
		int bgPatientCount = bgData.getPatientIdsCount();

		int exclusionListSize = 0;
		if (exclusionListTarget != null) {
			exclusionListSize += exclusionListTarget.size();
		}
		if (exclusionListBg != null) {
			exclusionListSize += exclusionListBg.size();
		}

		int count2exclude = 0;
		if (exclusionListTarget != null) {
			Enumeration<Integer> keys = exclusionListTarget.keys();
			while (keys.hasMoreElements()) {
				Integer key = keys.nextElement();
				count2exclude += targetData.getNzElement(key.intValue()); // targetData.nzElements[key];
			}
		}
		if (exclusionListBg != null) {
			Enumeration<Integer> keys = exclusionListBg.keys();
			while (keys.hasMoreElements()) {
				Integer key = keys.nextElement();
				count2exclude += bgData.getNzElement(key.intValue()); // bgData.nzElements[key];
			}
		}
		int nzElements = targetData.getTotalNzElementsCount()
				+ bgData.getTotalNzElementsCount() - count2exclude;// targetData.totalNzElementsCount
																	// +
																	// bgData.totalNzElementsCount
																	// -
																	// count2exclude;

		FeatureMatrix X = new FeatureMatrix(nzElements, 3);
		X.setColumnCount(targetData.getRowCount()); // number of snps -
													// assumption two datasets
													// match in size
		X.setRowCount(targetPatientCount + bgPatientCount - exclusionListSize); // first
																				// column
																				// is
																				// SNP
																				// id
		// X.colNum = targetData.getColumnCount(); //number of snps - assumption
		// two datasets match in size
		// X.rowNum = targetPatientCount+bgPatientCount - exclusionListSize;
		// //first column is SNP id

		// X.x = new double[nzElements][3]; //number of non zero elements

		int row = 0;
		int patient = 0; // need to be adapted to the excluded patients
		for (int patTar = 0; patTar < targetPatientCount; patTar++) {
			if (exclusionListTarget == null
					|| !exclusionListTarget.containsKey(patTar)) {
				patient++;
				for (int snp = 0; snp < X.getColumnCount(); snp++) { // running
																		// over
																		// all
																		// the
																		// snps
					// for (int snp=0; snp<X.colNum; snp++){ //running over all
					// the snps
					// if (targetData.data[snp][patTar]!=0) { //storing just
					// non-zero elements
					if (targetData.getData(snp, patTar) != 0) { // storing just
																// non-zero
																// elements
						// X.x[row][0] = patient;//column (or current patient) -
						// value is 1-index-based
						// X.x[row][1] = snp+1;//row (or curSnpCount) - value is
						// 1-index-based
						// X.x[row][2] = targetData.getData(snp, patTar);
						// //value only for non-zero elements
						X.setFeature(row, 0, patient); // column (or current
														// patient) - value is
														// 1-index-based
						X.setFeature(row, 1, snp + 1); // row (or curSnpCount) -
														// value is
														// 1-index-based
						X.setFeature(row, 2, targetData.getData(snp, patTar)); // value
																				// only
																				// for
																				// non-zero
																				// elements
						row++;
					}
				}
			}
		}
		for (int patBg = 0; patBg < bgPatientCount; patBg++) {
			if (exclusionListBg == null || !exclusionListBg.containsKey(patBg)) {
				patient++;
				for (int snp = 0; snp < X.getColumnCount(); snp++) { // running
																		// over
																		// all
																		// the
																		// snps
					// for (int snp=0; snp<X.colNum; snp++){ //running over all
					// the snps
					// if (bgData.data[snp][patBg]!=0) { //storing just non-zero
					// elements
					if (bgData.getData(snp, patBg) != 0) { // storing just
															// non-zero elements
						// X.x[row][0] = patient;//column (or current patient) -
						// value is 1-index-based
						// X.x[row][1] = snp+1;//row (or curSnpCount) - value is
						// 1-index-based
						// X.x[row][2] = bgData.getData(snp, patBg);//value only
						// for non-zero elements
						X.setFeature(row, 0, patient); // column (or current
														// patient) - value is
														// 1-index-based
						X.setFeature(row, 1, snp + 1); // row (or curSnpCount) -
														// value is
														// 1-index-based
						X.setFeature(row, 2, bgData.getData(snp, patBg)); // value
																			// only
																			// for
																			// non-zero
																			// elements
						row++;
					}
				}
			}
		}
		return X;
	}

	/**
	 * Creates a labels vector
	 * 
	 * @param targetData
	 * @param bgData
	 * @param exclusionListTarget
	 * @param exclusionListBg
	 * @return double[]
	 */
	public static double[] createLabelsVector(DataSetType targetData,
			DataSetType bgData,
			Hashtable<Integer, Boolean> exclusionListTarget,
			Hashtable<Integer, Boolean> exclusionListBg) {
		if (targetData == null || bgData == null) {
			return null;
		}
		int targetPatientCount = targetData.getPatientIdsCount(); // targetData.patientIds.length;
		int bgPatientCount = bgData.getPatientIdsCount(); // bgData.patientIds.length;
		int exclusionSizeTarget = 0;
		int exclusionSizeBg = 0;
		if (exclusionListTarget != null)
			exclusionSizeTarget = exclusionListTarget.size();
		if (exclusionListBg != null)
			exclusionSizeBg = exclusionListBg.size();
		double[] y = new double[targetPatientCount + bgPatientCount
				- exclusionSizeTarget - exclusionSizeBg];
		int targetFinalCount = targetPatientCount - exclusionSizeTarget;
		for (int tarPatient = 0; tarPatient < targetFinalCount; tarPatient++) {
			y[tarPatient] = 1; // positive sample
		}
		for (int bgPatient = 0; bgPatient < bgPatientCount - exclusionSizeBg; bgPatient++) {
			y[bgPatient + targetFinalCount] = -1; // negative sample
		}
		return y;
	}
}
