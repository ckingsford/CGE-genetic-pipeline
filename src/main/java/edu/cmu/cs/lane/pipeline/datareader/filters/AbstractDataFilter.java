package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.configuration.PropertiesConfiguration;

import edu.cmu.cs.lane.datatypes.dataset.SNProwDataBean;
import edu.cmu.cs.lane.pipeline.datareader.DataReaderPhaseFileInteractor;

abstract public class AbstractDataFilter implements DataReaderPhaseFileInteractor<AbstractDataFilter> {

	abstract public String getName();
	

	abstract public AbstractDataFilter create();

	abstract public boolean filter(VariantFilterBean filterDataBean, String rawLine);

	abstract public boolean requiresSnpVector();


	public void initializeDataFilter() throws Exception {
		String config = "filters/default-filter.properties";
		PropertiesConfiguration props = new PropertiesConfiguration();
		props.load(config);

		//THRESHOLD = (Double) props.getProperty("filter.threshold");

	}

	//private double THRESHOLD = 0.90; //TODO: not clear why this should be in the abstract class

	/*
	public ArrayList<SNProwDataBean> filterSNPs(ArrayList<SNProwDataBean> SNPrecord) {
		ArrayList<SNProwDataBean> filteredSNPs = new ArrayList<SNProwDataBean>();
		for (int i = 0; i < SNPrecord.size(); i++) {
			Vector<Byte> mutations = SNPrecord.get(i).getSnpVector();
			double qualityScore = this.applyFilterCriteria(mutations);
			System.out.println("Quality Score for " + SNPrecord.get(i).getRsId() + "\t" + qualityScore);
			if (qualityScore >= THRESHOLD) {
				filteredSNPs.add(SNPrecord.get(i));
			}
		}
		return filteredSNPs;
	}
	*/

	/*
	public double applyFilterCriteria(Vector<Byte> mutations) {
		int missingValCount = 0;
		for (int i = 0; i < mutations.size(); i++) {
			if (mutations.get(i) == -1) {
				missingValCount++;
			}
		}
		double percentCorrect = (double) missingValCount / mutations.size();
		return percentCorrect;
	}*/

}
