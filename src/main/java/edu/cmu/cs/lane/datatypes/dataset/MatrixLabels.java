package edu.cmu.cs.lane.datatypes.dataset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MatrixLabels {

	private String[] columnLabels;
	private String[] rowLabels;
	private String[] columnExtendedInfo;
	private String[] rowExtendedInfo;
	
	public MatrixLabels(){
		
	}

	public MatrixLabels(String filename) throws IOException {
		BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

		int row = 0;
		String line;
		while ((line = is.readLine()) != null) {
			if (row == 0) {
				String[] elements = line.split("\\s+");
				columnLabels = new String[elements.length - 1];
				for (int i = 1; i < elements.length; i++) {
					columnLabels[i - 1] = elements[i];
				}
			}
			row++;
		}
		is.close();

		if (row > 1) {
			rowLabels = new String[(row - 1)];
			row = 0;
			is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			while ((line = is.readLine()) != null) {
				String[] elements = line.split("\\s+");
				if (row != 0) {
					rowLabels[row - 1] = elements[0];
				}
				row++;
			}
		}
		columnExtendedInfo = null;
		rowExtendedInfo = null;
	}

	/**
	 * @return the columnExtendedInfo
	 */
	public String[] getColumnExtendedInfo() {
		return columnExtendedInfo;
	}

	/**
	 * @param columnExtendedInfo the columnExtendedInfo to set
	 */
	public void setColumnExtendedInfo(String[] columnExtendedInfo) {
		this.columnExtendedInfo = columnExtendedInfo;
	}

	/**
	 * @return the rowExtendedInfo
	 */
	public String[] getRowExtendedInfo() {
		return rowExtendedInfo;
	}

	/**
	 * @param rowExtendedInfo the rowExtendedInfo to set
	 */
	public void setRowExtendedInfo(String[] rowExtendedInfo) {
		this.rowExtendedInfo = rowExtendedInfo;
	}

	public String[] getColumnLabels() {
		return columnLabels;
	}

	public String[] getRowLabels() {
		return rowLabels;
	}

	public String toString() {
		String out = "";
		for (String label : columnLabels) {
			out += label + ", ";
		}
		out += "\n";
		for (String label : rowLabels) {
			out += label + ", ";
		}
		return out;
	}

	public void transpose() {
		String[] temp = columnLabels;
		columnLabels = rowLabels;
		rowLabels = temp;
		temp = columnExtendedInfo;
		columnExtendedInfo = rowExtendedInfo;
		rowExtendedInfo = temp;
		
	}
	public void setColumnLabels(String[] colLabels){
		this.columnLabels = colLabels.clone();
	}
	public void setRowLabels(String[] rowLabels){
		this.rowLabels = rowLabels.clone();
	}
	public void setColumnLabel(int index, String label){
		this.columnLabels[index] = label;
	}
	public void setRowLabel(int index, String label){
		this.rowLabels[index] = label;
	}
	public void setColumnExtLabel(int index, String label){
		if (this.columnExtendedInfo == null){	
			int size = Math.max(this.getColumnCount(), index+1);//guess max string size 
			this.columnExtendedInfo = new String[size];
		}
		if (index >= columnExtendedInfo.length){ //very unlikely as usually it will be instantiated in the same length of columns
			columnExtendedInfo = Arrays.copyOf(columnExtendedInfo, index+1);
		}
		this.columnExtendedInfo[index] = label;
	}
	public void setRowExtLabel(int index, String label){
		if (this.rowExtendedInfo == null){	
			int size = Math.max(this.getRowCount(), index+1);//guess max string size 
			this.rowExtendedInfo = new String[size];
		}
		if (index >= rowExtendedInfo.length){ //very unlikely as usually it will be instantiated in the same length of columns
			rowExtendedInfo = Arrays.copyOf(rowExtendedInfo, index+1);
		}
		this.rowExtendedInfo[index] = label;
	}
	public String getRowLabel(int index){
		return rowLabels[index];
	}
	public String getColumnLabel(int index){
		return columnLabels[index];
	}
	public String getRowExtLabel(int index){
		if (rowExtendedInfo != null)
			return rowExtendedInfo[index];
		return null;
	}
	public String getColumnExtLabel(int index){
		if (columnExtendedInfo != null)
			return columnExtendedInfo[index];
		return null;
	}
	
	public int getRowCount(){
		return rowLabels.length;
	}
	public int getColumnCount(){
		return columnLabels.length;
	}

	public MatrixLabels sliceByRows(int[] rindices) {
		MatrixLabels matrix = new MatrixLabels();
		matrix.columnLabels = Arrays.copyOf(columnLabels, columnLabels.length);
		if (columnExtendedInfo != null) matrix.columnExtendedInfo = Arrays.copyOf(columnExtendedInfo, columnExtendedInfo.length);
		matrix.rowLabels = new String[rindices.length];
		if (rowExtendedInfo != null) matrix.rowExtendedInfo = new String[rindices.length];
		
		for (int i=0; i<rindices.length; i++){
			matrix.rowLabels[i]=rowLabels[rindices[i]];
			if (rowExtendedInfo != null) matrix.rowExtendedInfo[i]=rowExtendedInfo[rindices[i]];
		}
		
		return matrix;
	}
	
	public MatrixLabels sliceByColumns(int[] cindices) {
		MatrixLabels matrix = new MatrixLabels();
		matrix.rowLabels = Arrays.copyOf(rowLabels, rowLabels.length);
		if (rowExtendedInfo != null) matrix.rowExtendedInfo = Arrays.copyOf(rowExtendedInfo, rowExtendedInfo.length);
		matrix.columnLabels = new String[cindices.length];
		if (columnExtendedInfo != null) matrix.columnExtendedInfo = new String[cindices.length];
		
		for (int i=0; i<cindices.length; i++){
			matrix.columnLabels[i]=columnLabels[cindices[i]];
			if (columnExtendedInfo != null) matrix.columnExtendedInfo[i]=columnExtendedInfo[cindices[i]];
		}
		
		return matrix;
	} 
}
