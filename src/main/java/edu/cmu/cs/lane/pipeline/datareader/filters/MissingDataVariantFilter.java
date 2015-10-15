package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.util.Vector;

import edu.cmu.cs.lane.datatypes.dataset.SNProwDataBean;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class MissingDataVariantFilter extends AbstractDataFilter {

	static boolean WARNING_PROVIDED = false;
	
	public MissingDataVariantFilter() {
	}

	@Override
	public String getName() {
		return "MissingDataVariant";
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
		
		return new MissingDataVariantFilter();
	}

	@Override
	public boolean filter(VariantFilterBean snpRow, String rawLine) {
		Vector<Byte> row = snpRow.getSnpVector();
		if (row != null){
			int countMissing = 0;
			for (int i = 0; i < row.size(); i++) {
				if (row.elementAt(i) == ((OptionsGeneral) OptionsFactory.getOptions("filters")).getMissingValueSymbol())
					countMissing++;
			}
			double missingFreq = 0.5; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("filters")).getMaxMissingFreq();
			if ((double) countMissing / row.size() >= missingFreq)
				return false;
			return true;
		}else{
			if (!WARNING_PROVIDED){
				System.out.println("WARNING: missing data filter was not provided.");
				WARNING_PROVIDED = true;
			}
			return true;
		}
	}
}
