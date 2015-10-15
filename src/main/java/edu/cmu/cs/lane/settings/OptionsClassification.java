/**
 * 
 */
package edu.cmu.cs.lane.settings;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author zinman
 *
 */
public class OptionsClassification extends AbstractOptions {
	private double[] lambdas;
	private int max_iterations;
	private double[] thresholds;
	private String[] algorithms;
	private int[] regression_path_lengths;
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "classification";
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
	public void readParams(PropertiesConfiguration config)
			throws ConfigurationException {
		
		if (config.containsKey("analysis-lambda")) {
			String[] strLambda = config.getString("analysis-lambda").split(",");
			if (strLambda.length > 0) {
				lambdas = new double[strLambda.length];
				for (int i = 0; i < strLambda.length; i++) {
					setLambda(i, Double.parseDouble(strLambda[i]));
				}
			}
		}
		if (config.containsKey("analysis-max-iterations")) {
			this.setMaxIterations(Integer.parseInt(config.getProperty(
					"analysis-max-iterations").toString()));
		}
		if (config.containsKey("analysis-threshold")) {
			String[] strThreshold = config.getString("analysis-threshold").split(",");
			if (strThreshold.length > 0) {
				thresholds = new double[strThreshold.length];
				for (int i = 0; i < strThreshold.length; i++) {
					setThreshold(i, Double.parseDouble(strThreshold[i]));
				}
			}
		}
		if (config.containsKey("analysis-algorithms")) {
			this.setAlgorithms((config.getProperty("analysis-algorithms")
					.toString().split("[\\s,;]+")));
		}
		if (config.containsKey("analysis-regression-path-length")) {
			String[] strRegressionPathLength = config
					.getString("analysis-regression-path-length").split(",");
			if (strRegressionPathLength.length > 0) {
				regression_path_lengths = new int[strRegressionPathLength.length];
				for (int i = 0; i < strRegressionPathLength.length; i++) {
					setRegressionPathLength(i,
							Integer.parseInt(strRegressionPathLength[i]));
				}
			}
		}
	}

	public double getLambda(int i) {
		return lambdas[i];
	}

	public void setLambda(int i, double lambda) {
		this.lambdas[i] = lambda;
	}

	public int getMaxIterations() {
		return max_iterations;
	}

	public void setMaxIterations(int max_iterations) {
		this.max_iterations = max_iterations;
	}
	public double[] getLambdas() {
		if (lambdas == null) {
			System.err.println("Warning 'lambdas' is null!");
		}
		return lambdas;
	}

	public double getThreshold(int i) {
		return thresholds[i];
	}

	public void setThreshold(int i, double threshold) {
		this.thresholds[i] = threshold;
	}
	public double[] getThresholds() {
		if (thresholds == null) {
			System.err.println("Warning 'thresholds' is null!");
		}
		return thresholds;
	}
	public String[] getAlgorithms() {
		if (algorithms == null) {
			System.err.println("Warning 'algorithms' is null!");
		}
		return algorithms;
	}

	public void setAlgorithms(String[] algorithms) {
		this.algorithms = algorithms;
	}


	public int getRegressionPathLength(int i) {
		return regression_path_lengths[i];
	}

	public void setRegressionPathLength(int i, int regression_path_length) {
		this.regression_path_lengths[i] = regression_path_length;
	}
	
	public int[] getRegressionPathLengths() {
		if (regression_path_lengths == null) {
			System.err.println("Warning 'regression_path_lengths' is null!");
		}
		return regression_path_lengths;
	}
}
