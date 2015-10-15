package edu.cmu.cs.lane.pipeline.datareader.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.cmu.cs.lane.datatypes.dataset.SNProwDataBean;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * 
 * 
 * 
 */
public class LookUpDataFilter extends AbstractDataFilter {

	private HashSet<String> hashRsIds;

	private HashMap<String, String> hashFullIds;

	public LookUpDataFilter() {
		hashRsIds = new HashSet<String>();
		hashFullIds = new HashMap<String, String>();
	}

	@Override
	public String getName() {
		return "LookUpDataFilter";
	}
	/** 
	 * @see edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter#requiresSnpVector()
	 */
	@Override
	public boolean requiresSnpVector() {
		return false;
	}

	

	@Override
	public AbstractDataFilter create() {
		LookUpDataFilter dataFilter = new LookUpDataFilter();
		String folderName = "folder"; //TODO:((OptionsGeneral) OptionsFactory.getOptions("filters")).getLookupListFolder();
		String filenamePattern = "filenamePattern"; //TODO:((OptionsGeneral) OptionsFactory.getOptions("filters")).getFilenamePattern();
		String filterBy = "filterBy"; //TODO:((OptionsGeneral) OptionsFactory.getOptions("filters")).getFilterBy();
		try {
			dataFilter.populateHash(folderName, filenamePattern, filterBy);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataFilter;
	}

	@Override
	public boolean filter(VariantFilterBean filterDataBean, String rawLine) {
		if (hashRsIds!=null && hashRsIds.contains(filterDataBean.getRsId()) || 
				hashFullIds!=null && hashFullIds.containsKey(filterDataBean.getFullId()))
			return true;
		return false;
	}

	public void populateHashByRsID(ArrayList<String> elements){
		for (int i=0; i<elements.size(); i++){
			hashRsIds.add(elements.get(i));		
		}
	}
	public void populateHashByPosition(ArrayList<String> elements){
		for (int i=0; i<elements.size(); i++){
			hashFullIds.put(elements.get(i), "");		
		}
	}
	public void populateHash(String folderName, String filenamePattern,
			String filterBy) throws IOException {
		File folder = new File(folderName);

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()
					&& fileEntry.getName().endsWith(filenamePattern)) {
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(
							fileEntry.getAbsoluteFile()));
					String line = br.readLine(); // header line 
					// if filtering should be done using RSID or position
					while ((line = br.readLine()) != null) {
						String rec[] = line.split("[\\t]");
						String rsId = rec[1].trim();
						if (!rsId.startsWith("rs")) {
							rsId = "rs" + rsId;
						}
						hashFullIds.put(rsId, rec[0].trim());
						hashRsIds.add(rsId);

					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						br.close();
						br = null;
					}
				}

			}
		}

	}

	public  HashMap<String, String> getHashFullIds() {
		return hashFullIds;
	}
}
