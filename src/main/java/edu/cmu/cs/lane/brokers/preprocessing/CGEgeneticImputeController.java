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
public class CGEgeneticImputeController extends AbstractCGEgeneticPreprocessingContorller {

	Hashtable<String, AbstractGeneticMissingValueImputer> imputers = new Hashtable<String, AbstractGeneticMissingValueImputer>();
	/**
	 * @see edu.cmu.cs.lane.brokers.preprocessing.AbstractCGEgeneticPreprocessingContorller#getName()
	 */
	@Override
	public String getName() {
		return "impute";
	}
	
	/**
	 * 
	 */
	public CGEgeneticImputeController() {
		ArrayList<String> imputerClasses = new ArrayList<String>();
		imputerClasses.add(SimpleMissingValueImputer.class.getCanonicalName());//TODO: get from an internal settings file
		imputerClasses.add(LDMissingValueImputer.class.getCanonicalName());
		imputerClasses.add(DistanceMissingValueImputer.class.getCanonicalName());
		try {
			for (String imputerClass: imputerClasses){
				imputers.put(FactoryUtils.<AbstractGeneticMissingValueImputer> classInstantiator(imputerClass).getName(),
					FactoryUtils.<AbstractGeneticMissingValueImputer> classInstantiator(imputerClass));
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
	public ArrayList<SamplesGeneticData> apply (ArrayList<SamplesGeneticData> data, boolean loadStore, String title){

		//load from checkpoint if requested
		if (loadStore == true){
		}

		//process
		String missingValueImputer = ((OptionsPreprocessing) OptionsFactory.getOptions("preprocessing")).getImputerName();
		int windowSize = ((OptionsPreprocessing) OptionsFactory.getOptions("preprocessing")).getWindowSize();
		AbstractGeneticMissingValueImputer imputer;
		try {
			imputer = imputers.get(missingValueImputer);
			imputer.setWindowSize(windowSize);
			data = imputer.imputeMissingValue(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//store checkpoint if requested
		if (loadStore == true){
			String storeTarget = "";
			//TODO: let the matrix store itself directly - keep information on which specific 
			CGEStoreGeneticDataCenter.storeDataToFile((SamplesGeneticData) data.get(0), storeTarget); //do it separately for target and background

		}
		return data;
	}


}
