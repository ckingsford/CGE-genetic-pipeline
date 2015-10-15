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
public abstract class AbstractEvaluationFunction {
	abstract public String getName();
	abstract public boolean requiresClassification();
	abstract public double evaluate(ArrayList<SamplesLabels> real, ArrayList<CGEPrediction> predicted);
}
