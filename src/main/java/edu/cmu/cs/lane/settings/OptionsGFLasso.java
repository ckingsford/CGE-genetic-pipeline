package edu.cmu.cs.lane.settings;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class OptionsGFLasso extends AbstractOptions { 

	// GFLasso
	private String network_negative_edge_weight_treatment;
	private double[] gammas;
	private double[] lambdas;
	private int max_iterations;
	private double tolerance;
	private int display_iteration;
	private double mu;
	private double network_correlation_threshold;
	private boolean standardizeBetas = true;
	
	
	/**
	 * @return the standardizeBetas
	 */
	public boolean isStandardizeBetas() {
		return standardizeBetas;
	}

	/**
	 * @param standardizeBetas the standardizeBetas to set
	 */
	public void setStandardizeBetas(boolean standardizeBetas) {
		this.standardizeBetas = standardizeBetas;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "GFLasso";
	}
	
	@Override
	public void readParams(PropertiesConfiguration config) throws ConfigurationException { //if more things are necessary
		//config.setListDelimiter(',');
		//System.out.println("list delimeter:" +config.getListDelimiter());
		if (config.containsKey("analysis-mu")) {
			this.setMu(Double.parseDouble(config.getProperty("analysis-mu")
					.toString()));
		}
		if (config.containsKey("analysis-network-correlation-threshold")) {
			this.setNetworkCorrelationThreshold(Double.parseDouble(config
					.getProperty("analysis-network-correlation-threshold")
					.toString()));
		}
		if (config
				.containsKey("analysis-network-negative-edge-weight-treatment")) {
			this.setNetworkNegativeEdgeWeightTreatment((config
					.getProperty("analysis-network-negative-edge-weight-treatment")
					.toString()));
		}
		if (config.containsKey("analysis-tolerance")) {
			this.setTolerance(Double.parseDouble(config.getProperty(
					"analysis-tolerance").toString()));
		}
		if (config.containsKey("analysis-display-iteration")) {
			this.setDisplayIteration(Integer.parseInt(config.getProperty(
					"analysis-display-iteration").toString()));
		}
		if (config.containsKey("analysis-gamma")) {
			String[] strGamma = config.getString("analysis-gamma").split(",");
			if (strGamma.length > 0) {
				gammas = new double[strGamma.length];
				for (int i = 0; i < strGamma.length; i++) {
					setGamma(i, Double.parseDouble(strGamma[i]));
				}
			}
		}
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
		if (config.containsKey("standardize-betas")) {
			this.setStandardizeBetas(Boolean.parseBoolean(config.getProperty(
					"standardize-betas").toString()));
		}

	}
	
	public double getNetworkCorrelationThreshold() {
		return network_correlation_threshold;
	}

	public void setNetworkCorrelationThreshold(
			double network_correlation_threshold) {
		this.network_correlation_threshold = network_correlation_threshold;
	}

	public String getNetworkNegativeEdgeWeightTreatment() {
		if (network_negative_edge_weight_treatment == null) {
			System.err
					.println("Warning 'network_negative_edge_weight_treatment' is null!");
		}
		return network_negative_edge_weight_treatment;
	}

	public void setNetworkNegativeEdgeWeightTreatment(
			String network_negative_edge_weight_treatment) {
		this.network_negative_edge_weight_treatment = network_negative_edge_weight_treatment;
	}

	public double getGamma(int i) {
		return gammas[i];
	}

	public void setGamma(int i, double gamma) {
		this.gammas[i] = gamma;
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

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public int getDisplayIteration() {
		return display_iteration;
	}

	public void setDisplayIteration(int display_iteration) {
		this.display_iteration = display_iteration;
	}

	

	public double getMu() {
		return mu;
	}

	public void setMu(double mu) {
		this.mu = mu;
	}

	public double[] getLambdas() {
		if (lambdas == null) {
			System.err.println("Warning 'lambdas' is null!");
		}
		return lambdas;
	}

	public double[] getGammas() {
		if (gammas == null) {
			System.err.println("Warning 'gammas' is null!");
		}
		return gammas;
	}


}
