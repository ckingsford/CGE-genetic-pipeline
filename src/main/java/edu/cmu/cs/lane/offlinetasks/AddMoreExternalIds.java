package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

import edu.cmu.cs.lane.brokers.store.MySQLStoreModule;

public class AddMoreExternalIds {

	public static void main(String args[]) {
		try {
			// AddMoreExternalIds main = new AddMoreExternalIds();
			MySQLStoreModule dbInfo = new MySQLStoreModule();
			BufferedReader bfr = new BufferedReader(new FileReader(
					"/media/alkesh/My Passport/GenRuption/genes/genes.tsv"));
			String str;
			bfr.readLine();
			while ((str = bfr.readLine()) != null) {

				try {
					String rec[] = str.split("[\\t]");
					String PAId = rec[0];
					String entrezId = rec[1].trim();

					if (entrezId.equals("")) {
						System.out.println("No corresponding entrezId for "
								+ str);
						continue;
					}

					String externalIds = rec[9];
					String ids[] = externalIds.split("[,]");
					HashMap<String, String> sourceIdMap = new HashMap<String, String>();
					for (int i = 0; i < ids.length; i++) {
						String sourceId[] = ids[i].trim().split("[:]");
						sourceIdMap.put(sourceId[0], sourceId[1]);
					}
					sourceIdMap.put("pharmgkb", PAId);
					HashMap<String, String> fieldMap = new HashMap<String, String>();
					fieldMap.put("geneId", entrezId);
					String selectedIds = "externalIds";
					HashMap<String, String> resultsMap = dbInfo.getGeneDBInfo(
							fieldMap, selectedIds);
					String dbExternalIds = resultsMap.get("externalIds");
					if (dbExternalIds == null) {
						System.out.println("Not found in DB " + dbExternalIds
								+ " for " + entrezId);
						String geneName = rec[3];
						String geneCode = rec[4];
						String otherNames = rec[5];
						String otherCodes = rec[6];

						HashMap<String, Object> insertMap = new HashMap<String, Object>();
						insertMap.put("geneId", Long.parseLong(entrezId));
						insertMap.put("geneCode", geneCode);
						long geneLocationId = dbInfo.getLocationId(Long
								.parseLong(entrezId));
						insertMap.put("geneLocationId", geneLocationId);
						insertMap.put("geneName", geneName);
						insertMap.put("otherNames", otherNames);
						insertMap.put("otherCodes", otherCodes);
						insertMap.put("externalIds", externalIds + "");
						String phenotypeIds = dbInfo.getPhenotypeId(Long
								.parseLong(entrezId));
						insertMap.put("phenotypeId", phenotypeIds);
						insertMap.put("source", "pharmgkb");

						dbInfo.insertGeneDBInfo(insertMap);
						// geneId,geneCode,geneLocationId,geneName,otherNames,otherCodes,externalIds,phenotypeId,source
						dbExternalIds = externalIds;
					}
					String dbExtIds[] = dbExternalIds.trim().split("[|,]");
					for (int i = 0; i < dbExtIds.length; i++) {
						String keyval[] = dbExtIds[i].trim().split("[:]");
						String dbSource = keyval[0];
						if (keyval.length < 2) {
							// System.out.println("Problem at "+dbExtIds[i]);
							continue;
						}
						String dbValue = keyval[1];
						if (!sourceIdMap.containsKey(dbSource)) {
							sourceIdMap.put(dbSource, dbValue);
						}
					}

					Iterator<String> it = sourceIdMap.keySet().iterator();
					String newExternalIds = "";
					while (it.hasNext()) {
						String key = it.next();
						String value = sourceIdMap.get(key);
						newExternalIds += key + ":" + value + "|";
					}
					newExternalIds = "'"
							+ newExternalIds.replaceAll("[|]$", "").trim()
							+ "'";
					// System.out.println(newExternalIds);
					HashMap<String, String> updateMap = new HashMap<String, String>();
					updateMap.put("externalIds", newExternalIds);
					updateMap.put("updationTime", "now()");
					dbInfo.updateGeneDBInfo(updateMap, fieldMap);

				} catch (Exception e1) {
					System.out.println("Error at " + str);
					e1.printStackTrace();
				}
			}
			bfr.close();
			bfr = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
