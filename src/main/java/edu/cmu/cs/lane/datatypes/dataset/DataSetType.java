package edu.cmu.cs.lane.datatypes.dataset;

public class DataSetType {
	private String[] snpIds;
	private String[] patientIds;
	private int[][] data;  //values should be 0,1,2
	private int[] nzElements; //number of non zero elements in the j-th column
	private int totalNzElementsCount; //for the entire dataset

	public DataSetType(int numberOfSnps, int numberOfPatients) {
	  snpIds = new String[numberOfSnps];
	  patientIds = new String[numberOfPatients];
	  data = new int [numberOfSnps][numberOfPatients];
	  nzElements = new int[numberOfPatients];
	  totalNzElementsCount = 0;
	}
	
	public int getRowCount() {
	  return data.length;
	}
	
	public int getData(int row, int col) {
	  return data[row][col];
	}
	
	public int getNzElement(int pos) {
	  return nzElements[pos];
	}
	
	public String getPatientId(int pos) {
	  return patientIds[pos];
	}
	
	public int getPatientIdsCount() {
	  return patientIds.length;
	}
	
	public String getSnpId(int pos) {
	  return snpIds[pos];
	}
	
	public int getSnpIdsCount() {
	  return snpIds.length;
	}
	
	public int getTotalNzElementsCount() {
	  return totalNzElementsCount;
	}
	
	public void setData(int row, int col, int value) {
	  data[row][col] = value;
	}
	
	public void setNzElement(int pos, int value) {
	  nzElements[pos] = value;
	}
	
	public void setPatientId(int pos, String patientId) {
	  patientIds[pos] = patientId;
	}
	
	public void setSnpId(int pos, String snpId) {
	  snpIds[pos] = snpId;
	}
	
	public void setTotalNzElementsCount(int value) {
	  totalNzElementsCount = value;
	}
}
