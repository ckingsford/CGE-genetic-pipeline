/**
 * 
 */
package edu.cmu.cs.lane.datatypes.dataset;

import java.util.ArrayList;

/**
 * @author zinman
 *
 */
public class SamplesDatasetList {
	ArrayList<SamplesDataset> samplesDatasets = null;
	
	/**
	 * 
	 */
	public SamplesDatasetList() {
	}
	
	public void initialize(int size) {
		samplesDatasets = new ArrayList<SamplesDataset>(size);
		for (int d=0; d<size; d++){
			samplesDatasets.add(new SamplesDataset());
		}
	}
	
	public void setSamplesLabels(ArrayList<SamplesLabels> labelsList){
		if (labelsList == null) {
			if (samplesDatasets != null){
				for (int d=0; d < samplesDatasets.size(); d++){
					samplesDatasets.set(d,  null); 
				}
			}
			return;
		}
		if (samplesDatasets == null) initialize(labelsList.size());
		if (samplesDatasets.size() != labelsList.size()){ 
			System.out.println("ERROR: the number of datasets is different from the number of assigned datasets labels (#SDL01)");
			return;
		}
		for (int d=0; d < labelsList.size(); d++){
			samplesDatasets.get(d).setLabelData(labelsList.get(d)); 
		}
	}
	
	public ArrayList<SamplesLabels> getSamplesLabelsList(){
		if (samplesDatasets == null) return null;
		ArrayList<SamplesLabels> samplesLabels = new ArrayList<SamplesLabels>();
		for (int d=0; d < samplesDatasets.size(); d++){
			samplesLabels.add(samplesDatasets.get(d).getLabelData()); 
		}
		return  samplesLabels;
	}
	
	public void setSamplesGeneticDataList(ArrayList<SamplesGeneticData> gdList){
		if (gdList == null) {
			if (samplesDatasets != null){
				for (int d=0; d < samplesDatasets.size(); d++){
					samplesDatasets.set(d,  null); 
				}
			}
			return;
		}
		if (samplesDatasets == null) initialize(gdList.size());
		if (samplesDatasets.size() != gdList.size()){ 
			System.out.println("ERROR: the number of datasets is different from the number of assigned genetic datasets (#SDL02)");
			return;
		}
		for (int d=0; d < gdList.size(); d++){
			samplesDatasets.get(d).setGeneticData(gdList.get(d)); 
		}
	}
	
	public ArrayList<SamplesGeneticData> getSamplesGeneticDataList(){
		ArrayList<SamplesGeneticData> gdList = new ArrayList<SamplesGeneticData>();
		for (int d=0; d < samplesDatasets.size(); d++){
			gdList.add(samplesDatasets.get(d).getGeneticData()); 
		}
		return gdList;
	}
	
	public void addSamplesDataset(SamplesDataset newDataset){
		if (samplesDatasets == null){
			samplesDatasets = new ArrayList<SamplesDataset>();
		}
		samplesDatasets.add(newDataset); 
	}
	
	public SamplesDataset get(int index){
		if (samplesDatasets == null || index>= samplesDatasets.size()) return null;
		return samplesDatasets.get(index);
	}
	
	public int size(){
		return samplesDatasets.size();
	}
}
