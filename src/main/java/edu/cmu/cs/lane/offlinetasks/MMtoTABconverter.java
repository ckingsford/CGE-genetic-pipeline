package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MMtoTABconverter {

	//mvn -f genetic-pipeline/pom.xml exec:java -Dexec.Xms256M -Dexec.Xmx1000M -Dexec.mainClass="edu.cmu.cs.lane.offlinetasks.MMtoTABconverter" -Dexec.args="./ ./ " 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputFolderName = args[0];
		File inFolder = new File(inputFolderName);
		String outputFolderName = args[1];
		String MMfilePatten = args[2];

		File[] inFiles = inFolder.listFiles();
		Pattern MMfilePat = Pattern.compile(MMfilePatten);
		File outFile;
		Matcher matcher;
		String outFileName;
		String chromosomeNum;
		for (File inFile : inFiles) {
			if (inFile.getName().endsWith("~")) {
				continue;
			}
			matcher = MMfilePat.matcher(inFile.getName());
			if (matcher.find()) {
				chromosomeNum = matcher.group(1);
				String VCFfileName = inputFolderName + "/chr" + chromosomeNum
						+ ".VCF.txt";
				File VCFfile = new File(VCFfileName);

				String patientsFileName = inputFolderName + "/patients.txt";
				File patientsFile = new File(patientsFileName);
				ArrayList<String> patients = getPatients(patientsFile);

				outFileName = outputFolderName + "/chr" + chromosomeNum
						+ ".TAB.txt";
				outFile = new File(outFileName);

				writeTabFile(VCFfile, inFile, patients, outFile);

			}
		}

	}

	private static void writeTabFile(File VCFfile, File inFile,
			ArrayList<String> patients, File outFile) {
		BufferedReader bfr;
		BufferedReader bVCF;
		try {
			bfr = new BufferedReader(new FileReader(inFile));
			bVCF = new BufferedReader(new FileReader(VCFfile));
			BufferedWriter bout = new BufferedWriter(new FileWriter(outFile));
			ArrayList<String> oneRow = new ArrayList<String>(patients.size());
			bout.write("SNP\t");
			for (int i = 0; i < patients.size(); i++) {
				oneRow.add("0");
				bout.write(patients.get(i) + "\t");
			}
			bout.write("\n");
			String[] tokens;
			String[] VCFtokens;
			String line;

			String curSNP = "0";
			String VCFline;
			while ((line = bfr.readLine()) != null) {
				tokens = line.split("\\s+");
				// read MM line
				if (!tokens[0].equals(curSNP)) { // if SNP has changed
					if (!curSNP.equals("0")) {
						// write TAB life
						VCFline = bVCF.readLine();
						VCFtokens = VCFline.split("\\t");
						bout.write(VCFtokens[2] + "\t"); // taking the RSid
						for (int i = 0; i < patients.size(); i++) {
							if (i > oneRow.size() || oneRow.get(i) == "0"
									|| oneRow.get(i) == null) {
								bout.write("0\t");
							} else {
								bout.write(oneRow.get(i) + "\t");
								oneRow.set(i, "0");
							}
						}
						bout.write("\n");
					}
					curSNP = tokens[0];
				}
				oneRow.set(Integer.parseInt(tokens[1]) - 1, tokens[2]);
			}
			// writing the last line
			// write TAB life
			VCFline = bVCF.readLine();
			VCFtokens = VCFline.split("\\t");
			bout.write(VCFtokens[2] + "\t"); // taking the RSid
			for (int i = 0; i < patients.size(); i++) {
				if (i > oneRow.size() || oneRow.get(i) == "0"
						|| oneRow.get(i) == null) {
					bout.write("0\t");
				} else {
					bout.write(oneRow.get(i) + "\t");
					oneRow.set(i, "0");
				}
			}
			bout.write("\n");

			bfr.close();
			bVCF.close();
			bout.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<String> getPatients(File patientsFile) {
		BufferedReader bfr;
		ArrayList<String> patients = new ArrayList<String>();
		try {
			bfr = new BufferedReader(new FileReader(
					patientsFile.getAbsolutePath()));
			String line;
			while ((line = bfr.readLine()) != null) {
				patients.add(line.trim());
			}
			bfr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patients;
	}

}
