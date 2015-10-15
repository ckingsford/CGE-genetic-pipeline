package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import edu.cmu.cs.lane.brokers.store.MySQLStoreModule;


public class ExtractGeneInfoTable {

				/*
	 			geneId BIGINT NOT NULL, #EntrenzId
				geneCode TEXT,
				geneLocationId BIGINT,
				geneName TEXT,
				otherNames TEXT,
				otherCodes TEXT,
				externalIds TEXT,
				phenotypeId TEXT,				
				*/
	
	public static void main(String args[]) {
		try {
				MySQLStoreModule dbInfo=new MySQLStoreModule();
			
				BufferedReader bfr=new BufferedReader(new FileReader("/media/alkesh/My Passport/GenRuption/Homo_sapiens.gene_info"));
				BufferedWriter bfw=new BufferedWriter(new FileWriter("geneinfo.txt"));
				String str;
				bfr.readLine();
				while((str=bfr.readLine())!=null){
					String rec[]=str.trim().split("[\\t]");
					long geneId=Long.parseLong(rec[1]);
					String geneCode=rec[2];
					long geneLocationId=dbInfo.getLocationId(geneId);
					String geneName=rec[8];
					String otherNames=rec[13];
					String otherCodes=rec[4];
					String externalIds=rec[5];
					String phenotypeIds=dbInfo.getPhenotypeId(geneId);					
					bfw.write(String.valueOf(geneId)+"\t"+
							 geneCode+"\t"+String.valueOf(geneLocationId)+"\t"+
							 geneName+"\t"+otherNames+"\t"+
							 otherCodes+"\t"+externalIds+"\t"+					  
							 String.valueOf(phenotypeIds)							
							);
					bfw.newLine();
				}
				bfr.close();
				bfr=null;
				bfw.close();
				bfw=null;
				
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
