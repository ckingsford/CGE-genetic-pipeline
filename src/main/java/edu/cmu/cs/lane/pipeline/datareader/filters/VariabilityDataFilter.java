package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.util.Vector;

import edu.cmu.cs.lane.datatypes.dataset.SNProwDataBean;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class VariabilityDataFilter extends AbstractDataFilter {

	public VariabilityDataFilter() {

	}

	@Override
	public String getName() {
		return "VariabilityDataFilter";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter#requiresSnpVector()
	 */
	@Override
	public boolean requiresSnpVector() {
		return true;
	}

	@Override
	public AbstractDataFilter create() {
		return new VariabilityDataFilter();
	}

	@Override
	public boolean filter(VariantFilterBean snpRow, String rawLine) {
		Vector<Byte> row = snpRow.getSnpVector();
		if (row != null){
			int[] freqs = new int[4];
			for (int i = 0; i < row.size(); i++) {
				freqs[row.elementAt(i)]++;
			}
			double minVariabilityFreq = 0.5; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("filters")).getMinVariabilityFreq()
			int realCounts = row.size() - freqs[((OptionsGeneral) OptionsFactory.getOptions("filters")).getMissingValueSymbol()];
			if ((double) freqs[0] / realCounts >= 1 - minVariabilityFreq 
					|| (double) freqs[2] / realCounts >= 1 - minVariabilityFreq)
				return false;
			return true;
		}else{
			System.out.println("WARNING: variability data filter was not provided.");
			return true;
		}
	}



}
