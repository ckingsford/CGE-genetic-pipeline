/**
 * 
 */
package edu.cmu.cs.lane.datatypes.evaluation;

import java.util.ArrayList;

import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.datatypes.prediction.CGEPrediction;

/**
 * @author zinman
 *
 */
public class EvaluateAccuracry extends AbstractEvaluationFunction {

	@Override
	public String getName(){
		return "accuracy";
	}

	/**
	 * @see edu.cmu.cs.lane.datatypes.evaluation.AbstractEvaluationFunction#requiresClassification()
	 */
	@Override
	public boolean requiresClassification() {
		return true;
	}

	/**
	 * @see edu.cmu.cs.lane.datatypes.evaluation.AbstractEvaluationFunction#evaluate()
	 */
	@Override
	public double evaluate(ArrayList<SamplesLabels> real, ArrayList<CGEPrediction> predicted) {
		// TODO Auto-generated method stub
		return 0;
	}
}
