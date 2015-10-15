/**
 * 
 */
package edu.cmu.cs.lane.brokers.store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.AnalysisSetDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;


/**
 * @author zinman
 *
 */
public class FileStoreModule extends AbstractStoreModule {

	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.brokers.AbstractStoreModule#getName()
	 */
	@Override
	public String getName() {
		return "file";
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.brokers.AbstractStoreModule#storeModel(edu.cmu.cs.lane.datatypes.CGEModel, edu.cmu.cs.lane.datatypes.AnalysisDetails)
	 */
	@Override
	public int storeModel(CGEModel model) {
		String outFolderS = ((OptionsGeneral) OptionsFactory.getOptions("general")).getOutputFolder();
		if (!outFolderS.endsWith("/")){outFolderS += "/";}
		File outFolder = new File(outFolderS);
		if (!outFolder.exists()){
			outFolder.mkdirs();
		}
		String outfile = "";
		String analysisName= ((OptionsGeneral) OptionsFactory.getOptions("general")).getAnalysisName().replaceAll("\\s+","_");	
		if (analysisName != null) outfile += analysisName +"_";
		if (model.getDetails().fileShortPostfix != null) outfile += model.getDetails().fileShortPostfix;
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getAddTimestampToOutput())
			outfile += "_" + new SimpleDateFormat("yyyyMMddhhmm").format(new Date()).toString();
		
		String resultsFile = outfile + "_results.txt";
		String detailsFile = outfile + "_details.txt";

		try {
			File file = new File(outFolder,resultsFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Feature id\tFeature type\tResponse variable\tValue\n");
			for (int i = 0; i < model.size(); ++i) {
				bw.write(model.getFeatures().get(i).id+"\t");
				bw.write(model.getFeatures().get(i).type+"\t");
				bw.write(model.getFeatures().get(i).var+"\t");
				bw.write(model.getFeatures().get(i).val+"\n");
			}
			bw.close();
			
			
			//Store details file
			file = new File(outFolder,detailsFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			JSONObject json = new JSONObject();
			try {
				json.put("alogrithmName", model.getDetails().algorithmName);
				json.put("alogrithmParameters", model.getDetails().algParameters);
				if (model.getDetails().additionalInfo!=null) json.put("additionalInfo", model.getDetails().additionalInfo);
				if (model.getDetails().targetSource!=null) json.put("targetSource", model.getDetails().targetSource);
				if (model.getDetails().backgroundSource!=null) json.put("backgroundSource", model.getDetails().backgroundSource);
				json.put("cvPercent", model.getDetails().cvPercent);
				json.put("avgAccuracy", model.getDetails().avgAccuracy);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			json.write(bw);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tResults written to file: " + outFolder +"/"+ resultsFile);
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tDetails written to file: " + outFolder +"/"+ detailsFile);

		return -1;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.brokers.AbstractStoreModule#storeSet(java.util.ArrayList, edu.cmu.cs.lane.datatypes.AnalysisSetDetails)
	 */
	@Override
	public int storeSet(ArrayList<Integer> analysesIds, AnalysisSetDetails analysisSetDetails) {
		String outFolderS = ((OptionsGeneral) OptionsFactory.getOptions("general")).getOutputFolder();
		if (!outFolderS.endsWith("/")){outFolderS += "/";}
		File outFolder = new File(outFolderS);
		if (!outFolder.exists()){
			outFolder.mkdirs();
		}
		String outfile = "";
		String analysisName= ((OptionsGeneral) OptionsFactory.getOptions("general")).getAnalysisName().replaceAll("\\s+","_");	
		if (analysisName != null) outfile += analysisName;
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).getAddTimestampToOutput())
			outfile += "_" + new SimpleDateFormat("yyyyMMddhhmm").format(new Date()).toString();		
		String detailsFile = outfile + "_setDetails.txt";
		try {
			File file = new File(outFolder,detailsFile);
			if (!file.exists()) {
				file.createNewFile();
			}
		
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			JSONObject json = new JSONObject();
		
			if (analysisSetDetails.name!=null)json.put("name", analysisSetDetails.name);
			if (analysisSetDetails.additionalInfo!=null)json.put("additionalInfo", analysisSetDetails.additionalInfo);
			json.write(bw);
			bw.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tSet details written to file: " + outFolder + "/"+ detailsFile);

		return -1;
	}

}
