package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.cmu.cs.lane.brokers.store.MySQLStoreModule;


public class GenePathwayExtraction {

	MySQLStoreModule dbInfo = new MySQLStoreModule();
	BufferedWriter pathwayInfo=null;
	BufferedWriter genePathwayRel=null;
	long pathwayId=1;
	public static void main(String args[]) {

		try {
			GenePathwayExtraction main = new GenePathwayExtraction();
			main.pathwayInfo=new BufferedWriter(new FileWriter("pathway-info.txt"));
			main.genePathwayRel=new BufferedWriter(new FileWriter("gene-pathway.txt"));
			
			BufferedReader bfr = new BufferedReader(
					new FileReader(
							"/media/alkesh/My Passport/GenRuption/pathways-tsv/pathways.tsv"));
			String str;
			String pathwayId = "";
			String pathwayDesc = "";
			String source = "";
			ArrayList<String> geneList = new ArrayList<String>();

			while ((str = bfr.readLine()) != null) {
				// String rec[]=str.split("[\\t]");
				str = str.trim();

				if (str.equals("")) {
					continue;
				}
				try {
					if (str.startsWith("PA")) {
						if (!pathwayId.equals("")) {
							main.printDetails(pathwayId, pathwayDesc, source,
									geneList);
							pathwayId = "";
							pathwayDesc = "";
							source = "";
							geneList.clear();
						}

						String rec[] = str.split("[:]");
						pathwayId = rec[0];
						String rec1[] = rec[1].split(" - ");
						pathwayDesc = rec1[0].trim();
						if(rec1.length>1){
							source = rec1[1].trim();
						}
					}

					if (str.startsWith("Gene")) {
						String rec[] = str.split("[\\t]");
						geneList.add(rec[1]);
					}
				} catch (Exception e) {
					System.out.println("Error at "+str);
					e.printStackTrace();
				}
			}

			bfr.close();
			bfr = null;
			main.pathwayInfo.close();
			main.pathwayInfo=null;
			main.genePathwayRel.close();
			main.genePathwayRel=null;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void printDetails(String pathwayPAId, String pathwayDesc,
			String source, ArrayList<String> geneList) throws Exception {

		String pathwayCategory="";
		String compounds="";
		String relatedPathways="";
		
		for (int i = 0; i < geneList.size(); i++) {

			String genePAId = geneList.get(i);
			long lngGeneId=dbInfo.getGeneIdFromPA(genePAId);
			System.out.println(lngGeneId+"\t"+pathwayPAId+"\t"+pathwayDesc+"\t"+source);
			String externalIds="pharmgkb:"+pathwayPAId;
			
			Double pValue=null;
			
			
			
			this.genePathwayRel.write(String.valueOf(lngGeneId)+"\t"+String.valueOf(pathwayId)+"\t"+
					externalIds+"\t"+pValue);
			this.genePathwayRel.newLine();

		}
		this.pathwayInfo.write(String.valueOf(pathwayId)+"\t"+pathwayDesc+"\t"+
				pathwayCategory+"\t"+compounds+"\t"+relatedPathways+"\t"+source);
		this.pathwayInfo.newLine();
		pathwayId++;
	}

}
