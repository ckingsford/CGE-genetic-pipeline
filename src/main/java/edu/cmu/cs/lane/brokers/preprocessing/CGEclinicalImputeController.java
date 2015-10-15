/**
 * 
 */
package edu.cmu.cs.lane.brokers.preprocessing;

import java.util.ArrayList;
import java.util.Hashtable;

import org.w3c.dom.views.AbstractView;

import edu.cmu.cs.lane.brokers.store.CGEStoreGeneticDataCenter;
import edu.cmu.cs.lane.datatypes.dataset.AbstractMatrixSampleDataType;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.pipeline.datapreprocessor.clinicalMissingvalue.AbstractClinicalMissingValueImputer;
import edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.AbstractGeneticMissingValueImputer;
import edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.DistanceMissingValueImputer;
import edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.LDMissingValueImputer;
import edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.SimpleMissingValueImputer;
import edu.cmu.cs.lane.pipeline.util.FactoryUtils;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsPreprocessing;

/**
 * A controller to handle preprocessing requests of imputing clinical data
 * @author zinman
 *
 */
public class CGEclinicalImputeController extends AbstractCGEclinicalPreprocessingContorller {

	Hashtable<String, AbstractClinicalMissingValueImputer> imputers = new Hashtable<String, AbstractClinicalMissingValueImputer>();
	/**
	 * @see edu.cmu.cs.lane.brokers.preprocessing.AbstractCGEgeneticPreprocessingContorller#getName()
	 */
	@Override
	public String getName() {
		return "imputeGenetic";
	}
	
	/**
	 * 
	 */
	public CGEclinicalImputeController() {
		ArrayList<String> imputerClasses = new ArrayList<String>();
			try {
			for (String imputerClass: imputerClasses){
				imputers.put(FactoryUtils.<AbstractClinicalMissingValueImputer> classInstantiator(imputerClass).getName(),
					FactoryUtils.<AbstractClinicalMissingValueImputer> classInstantiator(imputerClass));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}


	/**
	 * @see edu.cmu.cs.lane.brokers.preprocessing.AbstractCGEgeneticPreprocessingContorller#wasApplied(java.lang.String)
	 */
	@Override
	public boolean wasApplied(String title) {
		return false;
	}

	@Override
	public ArrayList<SamplesLabels> apply (ArrayList<SamplesLabels> data, boolean loadStore, String title){

		//load from checkpoint if requested
		if (loadStore == true){
		}

		//process
		String missingValueImputer = ((OptionsPreprocessing) OptionsFactory.getOptions("preprocessing")).getImputerName();
		AbstractClinicalMissingValueImputer imputer;
		try {
			imputer = imputers.get(missingValueImputer);
			data = imputer.imputeMissingValue(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//store checkpoint if requested
		if (loadStore == true){
			String storeTarget = "";
			//TODO: let the matrix store itself directly - keep information on which specific 
			//CGEStoreClinicalDataCenter.storeDataToFile((SamplesLabels) data.get(0), storeTarget); //do it separately for target and background

		}
		return data;
	}


}
