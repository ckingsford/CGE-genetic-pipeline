package edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class SimpleMissingValueImputer extends AbstractGeneticMissingValueImputer {

	private static Logger logger = Logger
			.getLogger(SimpleMissingValueImputer.class.getName());

	private int windowSize;

	@Override
	public String getName(){
		return "simpleImputer";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.AbstractGeneticMissingValueImputer#imputeMissingValue(java.util.ArrayList)
	 */
	@Override
	public ArrayList<SamplesGeneticData> imputeMissingValue(ArrayList<SamplesGeneticData> data) {
		
		//TODO: deal with the case of multiple chromosomes in the same data
		
		int fullWindowSize = windowSize * 2 + 1;
		for (int dataset = 0; dataset < data.size(); dataset++) {
			int sampleCount = data.get(dataset).getSamplesCount(); //deal with background as well;potentially after removed patients
			int MISSING_SYMBOL = ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueSymbol();
			ArrayList<LinkedList<Integer>> patientQueues = new ArrayList<LinkedList<Integer>>(sampleCount);
	
			// keeping counts for 0,1,2,?
			ArrayList<int[]> patientQueueCounts = new ArrayList<int[]>(sampleCount);
			LinkedList<Integer> SNPsQueue = new LinkedList<Integer>();
			for (int p = 0; p < sampleCount; p++) {
				patientQueues.add(new LinkedList<Integer>());
				patientQueueCounts.add(new int[4]);
			}
	
			int valPj;
			int removedVal;
	
			int writePosition = 0;
			int queuePosition = 0;
			int readingPosition = 0;
			for (int f = 0; f < data.get(dataset).getFeaturesCount(); f++) {
				SNPsQueue.addLast(f);
				for (int s = 0; s < sampleCount; s++) {
					valPj = (int) data.get(dataset).getData(s, f); //row index, col index
					patientQueues.get(s).addLast(valPj);
					if (valPj == MISSING_SYMBOL) {
						patientQueueCounts.get(s)[patientQueueCounts.get(s).length - 1]++; //counting the missing elements in the last index (as the symbol is not guaranteed to be 3
					} else {
						patientQueueCounts.get(s)[valPj]++;
					}
					
					
					//changing the counts combination of the current queue
					if (patientQueues.get(s).size() > fullWindowSize) { 
						removedVal = patientQueues.get(s).removeFirst(); //supposedly this value was dealt with and is no longer needed
						if (removedVal == MISSING_SYMBOL) {
							patientQueueCounts.get(s)[patientQueueCounts.get(s).length - 1]--;
						} else {
							patientQueueCounts.get(s)[removedVal]--;
						}
					}
				}
				
				if (SNPsQueue.size() > fullWindowSize) {
					SNPsQueue.removeFirst();
				}
				
				
				if (SNPsQueue.size() >= fullWindowSize) {// queue is full
					//write the beginning of the data once the queue is full
					while (writePosition < windowSize){ 
						int currentSNP = SNPsQueue.get(queuePosition);
						for (int pp = 0; pp < sampleCount; pp++) {
							if (patientQueues.get(pp).get(queuePosition) == MISSING_SYMBOL) {
								// found a missing value setting the filler value to be the max in the queue
								int newVal = 0; // in case of same number reference value wins
								if (patientQueueCounts.get(pp)[1] > patientQueueCounts.get(pp)[0]) {
									newVal = 1;
								} else if (patientQueueCounts.get(pp)[2] > patientQueueCounts.get(pp)[0] && patientQueueCounts.get(pp)[2] > patientQueueCounts.get(pp)[1]) {
									newVal = 2;
								}
								data.get(dataset).setData(pp,currentSNP,newVal);
							} else {
								data.get(dataset).setData(pp,currentSNP,patientQueues.get(pp).get(queuePosition));
							}
						}		
						queuePosition++;// queuePosition eventually need to be in the middle of the queue
						writePosition++; // just that we will know which line we just wrote
					}
					
					//write standard queue
					if (queuePosition == windowSize){ 
						int currentSNP = SNPsQueue.get(queuePosition);
						for (int pp = 0; pp < sampleCount; pp++) {
							if (patientQueues.get(pp).get(queuePosition) == MISSING_SYMBOL) {
								// found a missing value setting the filler value to be the max in the queue
								int newVal = 0; // in case of same number reference value wins
								if (patientQueueCounts.get(pp)[1] > patientQueueCounts.get(pp)[0]) {
									newVal = 1;
								} else if (patientQueueCounts.get(pp)[2] > patientQueueCounts.get(pp)[0] && patientQueueCounts.get(pp)[2] > patientQueueCounts.get(pp)[1]) {
									newVal = 2;
								}
								data.get(dataset).setData(pp,currentSNP,newVal);
							} else {
								data.get(dataset).setData(pp,currentSNP,patientQueues.get(pp).get(queuePosition));
							}
						}		
						writePosition++; // just that we will know which line we just wrote
					}
				}
				readingPosition++;
			}
			
			//we need to write the rest of the queue
			while (writePosition <= readingPosition) {
				int currentSNP = SNPsQueue.get(queuePosition);
				for (int pp = 0; pp < sampleCount; pp++) {
					if (patientQueues.get(pp).get(queuePosition) == MISSING_SYMBOL) {
						// found a missing value
						// setting the filler value to be the max in the queue
						int newVal = 0; // in case of same number reference value wins
						if (patientQueueCounts.get(pp)[1] > patientQueueCounts.get(pp)[0]) {
							newVal = 1;
						} else if (patientQueueCounts.get(pp)[2] > patientQueueCounts.get(pp)[0] && patientQueueCounts.get(pp)[2] > patientQueueCounts.get(pp)[1]) {
							newVal = 2;
						}
						data.get(dataset).setData(pp,currentSNP,newVal);
					} else {
						data.get(dataset).setData(pp,currentSNP,patientQueues.get(pp).get(queuePosition));
					}
				}
				writePosition++; // just that we will know which line we just wrote
				queuePosition++; // queuePosition should increase to the end of the queue
			}
		}
		return data;
	}
	
	@Override
	public void setWindowSize(int size) {
		windowSize = size;
	}



}
