package edu.cmu.cs.lane.pipeline.pipelineConfig;

import org.apache.log4j.xml.DOMConfigurator;

public abstract class PhaseController {
	public static final String LOG4J_CONFIG_PATH = "/log4j/log4j.xml";

	public abstract void runPhase();

	public PhaseController() {
		DOMConfigurator.configure(PhaseController.class.getResource(LOG4J_CONFIG_PATH));
	}
}
