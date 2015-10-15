package edu.cmu.cs.lane.pipeline.dataanalyzer;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDatasetList;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.datatypes.evaluation.AbstractEvaluationFunction;
import edu.cmu.cs.lane.datatypes.evaluation.EvaluationResult;
import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.prediction.CGEPrediction;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;

/**
 *  A simple example for code developers intended to show how the functionality of CGE can be extended by adding a new analyzer
 * 
 *  @author zinman
 *	@version 1.0
 *	@since 1.0
 */
public class ExampleAnalyzer extends AbstractAnalyzer {
	
	@Override
	public String getName() {
		return "ExampleAnalyzer";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#initializeAnalyzers(edu.cmu.cs.lane.datatypes.dataset.SamplesLabels, edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData)
	 */
	@Override
	public ArrayList<AbstractAnalyzer> initializeAnalyzers(SamplesDatasetList samplesDatasetList) {
		return null;
	}

	@Override
	public AbstractAnalyzer create() {
		ExampleAnalyzer example = new ExampleAnalyzer();	
		System.out.println(getName() + ": create()");
		return example;
	}

	@Override
	public ArrayList<SamplesLabels> loadLabelsOverride(ArrayList<SamplesLabels> labels) {
		return null;
	}
	
	@Override
	public ArrayList<SamplesGeneticData> loadDataOverride(ArrayList<SamplesGeneticData>  data, ArrayList<AbstractDataFilter> filters) {
		return null;
	}

	@Override
	public CGEModel analyze(SamplesDatasetList samplesDatasetList) {
		System.out.println(getName() + ": analyze()");
		return null;
	}

	@Override
	public ArrayList<CGEPrediction> applyModel(CGEModel model, SamplesDataset samplesDataset) {
		System.out.println(getName() + ": applyModel()");
		return null;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#testModel(edu.cmu.cs.lane.datatypes.model.CGEModel, java.util.ArrayList, java.util.ArrayList, edu.cmu.cs.lane.datatypes.CVindices)
	 */
	@Override
	public ArrayList<EvaluationResult> testModel(CGEModel model, SamplesDatasetList samplesDatasetList, ArrayList<AbstractEvaluationFunction>evaluateFunctions){
		return null;
	}

	
}
