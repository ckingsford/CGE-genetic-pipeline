/**
 * 
 */
package edu.cmu.cs.lane.datatypes.evaluation;

/**
 * @author zinman
 *
 */
public class EvaluationResult {
	String evaluteFunction;
	Double result;
	/**
	 * @return the evaluteFunction
	 */
	public String getEvaluteFunction() {
		return evaluteFunction;
	}
	/**
	 * @param evaluteFunction the evaluteFunction to set
	 */
	public void setEvaluteFunction(String evaluteFunction) {
		this.evaluteFunction = evaluteFunction;
	}
	/**
	 * @return the result
	 */
	public Double getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Double result) {
		this.result = result;
	}
}
