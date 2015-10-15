package edu.cmu.cs.lane.datatypes.shotgun;

/**
 * Feature Matrix stored in the matrix market format
 * @author Hank
 *
 */
public class FeatureMatrix {
	private double[][] x;
	private int rowNum;
	private int colNum;
	
	public FeatureMatrix(int rows, int columns) {
		x = new double[rows][columns];
	}
	
	public int getColumnCount() {
	  return colNum;
	}
	
	public double[][] getFeatureMatrix() {
	  return x;
	}
	
	public int getRowCount() {
	  return rowNum;
	}
	
	public void setColumnCount(int value) {
    colNum = value;
  }
	
	public void setFeature(int row, int col, double value) {
	  x[row][col] = value;
	}
	
	public void setRowCount(int value) {
	  rowNum = value;
	}
}
