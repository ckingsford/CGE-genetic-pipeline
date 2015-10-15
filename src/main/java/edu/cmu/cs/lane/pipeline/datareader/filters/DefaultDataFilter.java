package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.configuration.PropertiesConfiguration;

import edu.cmu.cs.lane.datatypes.dataset.SNProwDataBean;

/**
 * 
 * @author alkesh
 * 
 */
public abstract class DefaultDataFilter {

/*
extends AbstractDataFilter {

	
	double THRESHOLD = 0.10;// 10% threshold for missing value level

	public DefaultDataFilter() {
		try {
			this.initializeDataFilter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initializeDataFilter() throws Exception {
		String config = "";
		PropertiesConfiguration props = new PropertiesConfiguration();
		props.load(config);

		THRESHOLD = (Double) props.getProperty("filter.threshold");

	}

	@Override
	public ArrayList<SNProwDataBean> filterSNPs(ArrayList<SNProwDataBean> SNPrecord) {
		System.out
				.println("################ Inside Default Snps Filter ####################");
		ArrayList<SNProwDataBean> filteredSNPs = new ArrayList<SNProwDataBean>();
		for (int i = 0; i < SNPrecord.size(); i++) {
			Vector<Byte> mutations = SNPrecord.get(i).getSnpVector();
			double missingScore = this.applyFilterCriteria(mutations);
			if (missingScore <= THRESHOLD) {
				// System.out.println("Quality Score for "+snipRecord.get(i).getRsId()+"\t"+missingScore);
				filteredSNPs.add(SNPrecord.get(i));
			}

		}
		System.out.println("##### Out of Default SNPs Filter: "
				+ (SNPrecord.size() - filteredSNPs.size()) + " Filtered. "
				+ "#######");
		return filteredSNPs;
	}

	public double applyFilterCriteria(Vector<Byte> mutations) {
		System.out.println("##### Inside filter criteria ######");
		int missingValCount = 0;
		for (int i = 0; i < mutations.size(); i++) {
			if (mutations.get(i) == -1) {
				missingValCount++;
			}
		}
		double percentMissing = (double) missingValCount / mutations.size();
		System.out.println("####### Out of filter criteria " + percentMissing
				+ " ######");
		return percentMissing;
	}

	public String getName() {
		return "DefaultDataFilter";
	}
	*/
}
