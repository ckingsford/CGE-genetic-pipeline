package edu.cmu.cs.lane.pipeline.datapreprocessor.patient;

import java.util.List;

import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.pipeline.datareader.filters.SampleFilterBean;

public interface AbstractSampleFilter {

	abstract public String getName();
	
	abstract public boolean filter(SampleFilterBean sampleBean);
	
}
