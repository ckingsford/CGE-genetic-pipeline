/**
 * 
 */
package edu.cmu.cs.lane.datatypes.dataset;

/**
 * @author zinman
 *
 */
public class SamplesDataset {
	SamplesGeneticData geneticData;
	SamplesLabels labelData;
	
	public SamplesDataset(){
	
	}
	
	public SamplesDataset(SamplesDataset o){
		this.geneticData = o.geneticData;
		this.labelData = o.labelData;
	}
	
	/**
	 * @return the geneticData
	 */
	public SamplesGeneticData getGeneticData() {
		return geneticData;
	}

	/**
	 * @param geneticData the geneticData to set
	 */
	public void setGeneticData(SamplesGeneticData geneticData) {
		if (this.labelData == null){
			this.geneticData = geneticData;
		}else{
			if (this.labelData.getSamplesCount() != geneticData.getSamplesCount()) {
				System.out.println("ERROR: the number of samples in the clinical input is different from the number of samples in the genetic input (#SD03)");
				return;
			}
			//check if labels are sorted the same way
			boolean identical = true;
			for (int i =0; i < this.labelData.getSamplesCount(); i++){
				if (!this.labelData.getSampleName(i).equalsIgnoreCase(geneticData.getSampleName(i))){
					identical = false;
				}
			}
			if (identical){
				this.geneticData = geneticData;
			}
			else{
				System.out.println("ERROR: the samples in the clinical and genetic data are not sorted the same way (#SD04)");
				return;
			}
		}
		
	}
	/**
	 * @return the labelData
	 */
	public SamplesLabels getLabelData() {
		return labelData;
	}
	/**
	 * @param labelData the labelData to set
	 */
	public void setLabelData(SamplesLabels labelData) {
		if (this.geneticData == null)
			this.labelData = labelData;
		else{
			if (this.geneticData.getSamplesCount() != labelData.getSamplesCount()) {
				System.out.println("ERROR: the number of samples in the clinical input is different from the number of samples in the genetic input (#SD01)");
				return;
			}
			//check if labels are sorted the same way
			boolean identical = true;
			for (int i =0; i < this.geneticData.getSamplesCount(); i++){
				if (!this.geneticData.getSampleName(i).equalsIgnoreCase(labelData.getSampleName(i))){
					identical = false;
				}
			}
			if (identical){
				this.labelData = labelData;
			}
			else{
				System.out.println("ERROR: the samples in the clinical and genetic data are not sorted the same way (#SD02)");
				return;
			}
			
		}
			
	}
	
	public SamplesDataset sliceBySamples(int[] rindices){
		SamplesDataset newData = new SamplesDataset();
		newData.geneticData = new SamplesGeneticData(geneticData.sliceBySamples(rindices));
		newData.labelData = new SamplesLabels(labelData.sliceBySamples(rindices));
		return newData;
	}
	
	public SamplesDataset sliceByFeatures(int[] cindices){
		SamplesDataset newData = new SamplesDataset();
		newData.geneticData = new SamplesGeneticData(geneticData.sliceByFeatures(cindices));
		newData.labelData = new SamplesLabels(labelData.sliceByFeatures(cindices));
		return newData;
	}
	
	public void transpose(){
		geneticData.transpose();
		labelData.transpose();
	}
}
