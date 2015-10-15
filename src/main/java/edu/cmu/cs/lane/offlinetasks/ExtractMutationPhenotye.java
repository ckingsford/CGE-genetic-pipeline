package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import edu.cmu.cs.lane.brokers.store.MySQLStoreModule;


public class ExtractMutationPhenotye {

	public static void main(String args[]) {

		try {
			MySQLStoreModule dbInfo = new MySQLStoreModule();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(
					"phenotype.txt"));
			BufferedWriter bfw1 = new BufferedWriter(new FileWriter(
					"genotype_phenotype.txt"));
			BufferedReader bfr = new BufferedReader(
					new FileReader(
							"/media/alkesh/My Passport/GenRuption/mart_export_SNP-GWAS-disease.txt"));
			String str;
			bfr.readLine();// skip header;
			int count = 0;
			while ((str = bfr.readLine()) != null) {

				try {
					String rec[] = str.split("[\\t]");
					long position = Long.parseLong(rec[2]);
					String phenoTypeName = rec[8].trim();
					String phenoTypeDesc = rec[9].trim();
					long geneId = dbInfo.getGeneId(position);
					Double pValue = null;
					if(rec.length>12){
						pValue=Double.parseDouble(rec[12]);
					}
					String externalIds = rec[4];
					String source = "GWAS";

					bfw.write(String.valueOf(count) + "\t" + 
							String.valueOf(geneId)+"\t"+
							phenoTypeName + "\t" + 
							phenoTypeDesc + "\t" + externalIds + "\t"
							+ source);
					bfw.newLine();
					bfw1.write(String.valueOf(geneId) + "\t"
							+ String.valueOf(count) + "\t"
							+ String.valueOf(pValue));
					bfw1.newLine();
				} catch (Exception e) {
					System.out.println(str);
				}
				count++;

			}
			bfr.close();
			bfr = null;
			bfw.close();
			bfw = null;
			bfw1.close();
			bfw1 = null;
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
