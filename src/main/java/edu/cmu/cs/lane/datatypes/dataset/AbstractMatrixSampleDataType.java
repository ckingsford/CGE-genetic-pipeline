/**
 * 
 */
package edu.cmu.cs.lane.datatypes.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.jblas.DoubleMatrix;

/**
 * @author zinman
 *
 */
public class AbstractMatrixSampleDataType extends AbstractSampleDataType {
	protected DoubleMatrix data = null; //TODO - consider converting into FloatMatrix() or creating an abstract object that will encapsulate the matrix and will also something like IntMatrix()
	protected MatrixLabels labels = null;

	/**
	 * default constructor. Instantiate the samples hash
	 */
	public AbstractMatrixSampleDataType() {
		data =  new DoubleMatrix(); 
		labels = new MatrixLabels();
	}
	
	public AbstractMatrixSampleDataType(int samplesCount, int featuresCount) {
		initialize(samplesCount, featuresCount);
	}
	
	public void initialize(int samplesCount, int featuresCount) {
		initializeData(samplesCount, featuresCount);
		initializeFeatures(featuresCount);
	}
	public void initializeDataOnly(int samplesCount, int featuresCount) {
		initializeData(samplesCount, featuresCount);
	}
	public void initialize(String[] sampleNames, int featuresCount){
		initializeData(sampleNames.length, featuresCount);
		labels.setRowLabels(sampleNames);
		initializeFeatures(featuresCount);
	}
	
	public void initialize(int samplesCount, String[] featuresNames){
		initializeData(samplesCount, featuresNames.length);
		labels.setRowLabels(new String[samplesCount]);
		labels.setRowExtendedInfo(new String[samplesCount]);
		labels.setColumnLabels(featuresNames);
		labels.setColumnExtendedInfo(new String[featuresNames.length]);
	}
	
	public void initialize(String[] sampleNames, String[] featuresNames){
		initializeData(sampleNames.length, featuresNames.length);
		labels.setRowLabels(sampleNames);
		labels.setRowExtendedInfo(new String[sampleNames.length]);
		labels.setColumnLabels(featuresNames);
		labels.setColumnExtendedInfo(new String[featuresNames.length]);
	}
	
	public void initializeData(int samplesCount, int featuresCount) {
		data = new DoubleMatrix(samplesCount , featuresCount);
		labels.setRowLabels(new String[samplesCount]);
	}
	
	public void initializeSamples(int samplesCount){
		labels.setRowLabels(new String[samplesCount]);
	}
	
	public void initializeFeatures(int featuresCount){
		labels.setColumnLabels(new String[featuresCount]);
		labels.setColumnExtendedInfo(new String[featuresCount]);
	}
	
	public int getSamplesCount(){
		return data.getRows();
	}
	
	public int getFeaturesCount(){
		return data.getColumns();
	}
	
	public void transpose(){ 
		data = data.transpose();
		labels.transpose();
	}
	
	public AbstractMatrixSampleDataType sliceBySamples(int[] rindices){
		AbstractMatrixSampleDataType newData = new AbstractMatrixSampleDataType();
		newData.data = data.getRows(rindices);
		newData.labels = labels.sliceByRows(rindices);
		return newData;
	}
	
	public AbstractMatrixSampleDataType sliceByFeatures(int[] cindices){
		AbstractMatrixSampleDataType newData = new AbstractMatrixSampleDataType();
		newData.data = data.getColumns(cindices);
		newData.labels = labels.sliceByColumns(cindices);
		return newData;
	}
		
	public String[] getFeatureNames(){
		return labels.getColumnLabels();
	}
	
	public void setFeatureNames(String[] featureNames){
		this.labels.setColumnLabels(featureNames);
	}
	
	public void setFeatureNames(ArrayList<String> featureNames){
		this.labels.setColumnLabels(featureNames.toArray(new String[featureNames.size()]));
	}
	
	public String getFeatureName(int index){
		return labels.getColumnLabel(index);
	}
	
	public void setFeatureName(int index, String label){
		labels.setColumnLabel(index, label);
	}
	
	public String getExtendedFeatureInfo(int index){
		return labels.getColumnExtLabel(index);
	}
	
	public void setExtendedFeatureInfo(int index, String info){
		labels.setColumnExtLabel(index, info);
	}
	
	public String getExtendedSampleInfo(int index){
		return labels.getRowExtLabel(index);
	}
	
	public void setExtendedSampleInfo(int index, String info){
		labels.setRowExtLabel(index, info);
	}
	
	public String getSampleName(int index){
		return labels.getRowLabel(index);
	}
	public void setSampleName(int index, String sampleName){
		labels.setRowLabel(index, sampleName);
	}
	
	public String[] getSamplesNames(){
		return labels.getRowLabels();
	}
	
	public void setSamplesNames(String[] samplesNames){
		labels.setRowLabels(samplesNames);
	}
	
	public void setSamplesNames(ArrayList<String> samplesNames){
		this.labels.setRowLabels(samplesNames.toArray(new String[samplesNames.size()]));
	}

	
	public double getData(int r, int c){
		return data.get(r,c);
	}

	public DoubleMatrix ExtractDataAsMatrix(){
		return data;
	}
	
	public void setData(int r, int c, double val){
		data.put(r,c,val);
	}

	public void storeToFile(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			bw.write("Variant\t");
			bw.write(StringUtils.join(labels.getColumnLabels(), "\t"));
			bw.write("\n");
			for (int r = 0; r < labels.getRowCount(); r++){
				bw.write(labels.getRowLabel(r));
				for (int c = 0; c < data.getColumns(); c++){
					bw.write("\t" + data.get(r,c));
				}
				bw.write("\n");
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
