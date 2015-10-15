package edu.cmu.cs.lane.pipeline.datapreprocessor.patient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.cmu.cs.lane.pipeline.datareader.filters.SampleFilterBean;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class MissingDataSampleFilter implements AbstractSampleFilter {

	private static Logger logger = Logger.getLogger(MissingDataSampleFilter.class
			.getName());


	private double missingDataCutoff;
	
	public void setMissingDataCutoff(double misCutoff) {
		this.missingDataCutoff = misCutoff;
	}


	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.patient.AbstractSampleFilter#getName()
	 */
	@Override
	public String getName() {
		return "missingData";
	}


	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.patient.AbstractSampleFilter#filter(edu.cmu.cs.lane.pipeline.datareader.filters.SampleFilterBean)
	 */
	@Override
	public boolean filter(SampleFilterBean sampleBean) {
		int countMissing = 0;
		Byte missingSymbol = (byte) ((OptionsGeneral) OptionsFactory.getOptions("general")).getMissingValueSymbol();
		for (int i=0; i<sampleBean.getGeneticVariants().size(); i++){
			if (sampleBean.getGeneticVariants().get(i) == missingSymbol) countMissing ++;
		}
		return countMissing / sampleBean.getGeneticVariants().size() > missingDataCutoff;
	}



}
