/**
 * 
 */
package edu.cmu.cs.lane.brokers.store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * @author zinman
 *
 */
public class CGEStoreGeneticDataCenter {
	/**
	 * Stores data in a tab format
	 */
	static public void storeDataToFile(SamplesGeneticData data, String storeTarget){	
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.print("\tWriting results to file: " + storeTarget);
		try {
			File file = new File(storeTarget);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			bw.write("ID");
			for (String column : data.getFeatureNames()){
				bw.write("\t"+column);
			}
			
			for (int i = 0; i < data.getSamplesCount(); ++i) {
				bw.write(data.getFeatureName(i));
				for (int j = 0; j < data.getFeaturesCount(); ++j) {
					bw.write("\t"+data.getData(i, j));	
				}
				bw.write("\n");
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
    static public void storeDataToDatabase(){
    	//a possibility to consider
	}
}
