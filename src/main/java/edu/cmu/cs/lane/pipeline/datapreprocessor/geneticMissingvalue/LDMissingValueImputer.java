package edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import resources.LDresource;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsPreprocessing;

/**
 * LDimputer is based on DistanceMissingValueImputer but takes into account only SNPs that are in high LD in the distance window 
 */

public class LDMissingValueImputer extends AbstractGeneticMissingValueImputer {
	
	private int windowSize;
	private boolean isWeightedImputation;

	
	@Override
	public String getName(){
		return "LDImputer";
	}

	@Override
	public void setWindowSize(int size) {
		windowSize = size;
	}
	
	private static Logger logger = Logger
			.getLogger(DistanceMissingValueImputer.class.getName());



	/**
	 * 
	 */
	public LDMissingValueImputer() { 
		isWeightedImputation = ((OptionsPreprocessing) OptionsFactory.getOptions("preprocessing")).isWeightedImputation();
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.AbstractGeneticMissingValueImputer#imputeMissingValue(java.util.ArrayList)
	 */
	@Override
	public ArrayList<SamplesGeneticData> imputeMissingValue(ArrayList<SamplesGeneticData> data) {
		//SNPs are sorted by position
		int countImputed = 0;
		
		for (int dataset = 0; dataset < data.size(); dataset++) {
			int sampleCount = data.get(dataset).getSamplesCount(); //deal with background as well;potentially after removed patients
			int MISSING_SYMBOL = ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueSymbol();
			ArrayList<LinkedList<Integer>> patientQueues = new ArrayList<LinkedList<Integer>>(sampleCount);
	
			LinkedList<Integer> SNPsQueue = new LinkedList<Integer>();
			LinkedList<Long> positionQueue = new LinkedList<Long>();
			for (int p = 0; p < sampleCount; p++) {
				patientQueues.add(new LinkedList<Integer>());
			}
	
			int queueIndex = 0;
			for (int f = 0; f < data.get(dataset).getFeaturesCount(); f++) {	
				String snp = data.get(dataset).getFeatureName(f);
				String[] tokens = snp.split("\\."); //assumption the id is chr.position; 
				//make sure LDresource is initialized with the relevant chr
				LDresource.initialize(tokens[0]); //if LDresource is already initialized with the right Chr it will return immediately 
				Long newSNPpos = Long.parseLong(tokens[1]);
				
				//if queue is empty
				long currentSNPpos = newSNPpos; //just for initialization
				if(SNPsQueue.size() > 0) currentSNPpos = positionQueue.get(queueIndex);

				boolean lastAdded = false; //flag in order not to include last more than one time
				boolean exitFinalLoop = false;
				int valPj;
				//check distance between the position of the current imputed position and the new read SNP position
				while ((newSNPpos - currentSNPpos > windowSize && f != data.get(dataset).getFeaturesCount()-1) || (f == data.get(dataset).getFeaturesCount()-1 && !exitFinalLoop)){ //if we are in the last SNP 							
					if (f == data.get(dataset).getFeaturesCount()-1 && !lastAdded){ //if we are in the last SNP
						if (newSNPpos - currentSNPpos <= windowSize){
							//add the last to the queue
							positionQueue.addLast(newSNPpos);
							SNPsQueue.addLast(f);
							lastAdded = true;
							//add patients queue info
							for (int s = 0; s < sampleCount; s++) {
								valPj = (int) data.get(dataset).getData(s, f); //row index, col index
								patientQueues.get(s).addLast(valPj);
							}
						}
					}
					
					if(positionQueue.size() > 0){
						boolean imputed = false;
						//impute currentSNP based on current queue if needed
						for (int pp = 0; pp < sampleCount; pp++) {
							if (patientQueues.get(pp).get(queueIndex) == MISSING_SYMBOL) {
								// found a missing value setting the filler value to be the max in the queue
								//gather stats on all the SNPs in the queue for the specific sample
								double[] queueStats = new double[3]; //0,1,2
								for(int i=0; i<positionQueue.size(); i++){
									if (i!=queueIndex && patientQueues.get(pp).get(i) != MISSING_SYMBOL){ //skipping the index to be imputed
										if(isWeightedImputation){ //weighted - add LD value
											double ld = LDresource.getLD(positionQueue.get(i), positionQueue.get(queueIndex));
											if (ld > 0) queueStats[patientQueues.get(pp).get(i)]+=ld;//the stronger the relation the higher the LD
										}else if (LDresource.isLD(positionQueue.get(i), positionQueue.get(queueIndex))){
												queueStats[patientQueues.get(pp).get(i)]++; //otherwise ignore this SNP as it has no relevant association with the imputed SNP		
										}
									}
								}
								//define newVal based on the LD between SNPs
								int newVal = 0; // in case of same number reference value wins
								if (queueStats[1] > queueStats[0]) {
									newVal = 1;
								} else if (queueStats[2] > queueStats[0] && queueStats[2] > queueStats[1]) {
									newVal = 2;
								}
								data.get(dataset).setData(pp,SNPsQueue.get(queueIndex),newVal);
								imputed = true;
								countImputed++;
							} else {
								data.get(dataset).setData(pp,SNPsQueue.get(queueIndex),patientQueues.get(pp).get(queueIndex));
							}
						}	
						/* for debugging purposes
						//print queue for debugging purposes
						System.out.print(currentSNPpos + " => [");
						for (int q=0; q<positionQueue.size();q++){
							System.out.print(positionQueue.get(q)+",");
						}
						System.out.println("]");
						*/
						//if no SNP, i.e. queue is empty in the LD distance AND needs to be imputed set to 0 - give warning
						if (positionQueue.size() == 1 && imputed){
							System.out.println("WARNING: no SNPs are in the vicinity of " + data.get(dataset).getFeatureName(SNPsQueue.get(0)) + " based on the imputation window size defined. You should consider increasing the imputation window size.");
						}
						
						if (queueIndex == positionQueue.size()-1){
							//the current queue is not relevant to the newSNPpos - flush the queue
							queueIndex = 0;
							currentSNPpos = newSNPpos; //temporary filler to get out of the loop
							positionQueue.clear();
							SNPsQueue.clear();
							if (lastAdded){exitFinalLoop = true;}
							//clear patientQueue
							for (int pp = 0; pp < sampleCount; pp++) {
								patientQueues.get(pp).clear();
							}
						}else{
							//advance the queue pointer
							queueIndex++;
							currentSNPpos = positionQueue.get(queueIndex);
		
							//remove the beginning of the queue if needed
							long beginQueueDistance = windowSize+1;
							for (int removeIndex = 0; removeIndex < queueIndex && beginQueueDistance > windowSize; removeIndex++){
								beginQueueDistance = currentSNPpos - positionQueue.get(removeIndex);
								if (beginQueueDistance > windowSize){
									queueIndex --;
									removeIndex --;
									if (queueIndex < 0) queueIndex = 0;
									SNPsQueue.removeFirst();
									positionQueue.removeFirst();									
									//remove patientQueues
									for (int pp = 0; pp < sampleCount; pp++) {
										//changing the counts combination of the current queue
										patientQueues.get(pp).removeFirst(); //supposedly this value was dealt with and is no longer needed
									}
								}
							}
						}
					}
				}
				//add new SNP to the queue
				positionQueue.addLast(newSNPpos);
				SNPsQueue.addLast(f);
				//add patients queue info
				for (int s = 0; s < sampleCount; s++) {
					valPj = (int) data.get(dataset).getData(s, f); //row index, col index
					patientQueues.get(s).addLast(valPj);
				}
			}			
		}
	
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()){
			System.out.println(countImputed + " missing values were imputed using LD imputation.");
		}
		
		return data;
	}
}
