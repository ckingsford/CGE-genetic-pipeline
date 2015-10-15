package edu.cmu.cs.lane.offlinetasks.matchIDs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

public class ExtractRsIdFromDiseaseTable extends AbstractDataExtractor {
	private static final Logger logger = Logger
			.getLogger(ExtractRsIdFromDiseaseTable.class.getName());

	@Override
	public String getName() {
		return "ExtractRsIdFromDiseaseTable";
	}

	public AbstractDataExtractor create(String propertiesFile) {
		return new ExtractRsIdFromDiseaseTable();
	}

	@Override
	public void read(String targetDataLocation, String backgroundDataLocation,
			String fileFilterPattern, String outputFolder) {
		// Takes inputs directory as first argument and output directory ass second argument
		try {
			Hashtable<String, Integer> diseaseRsids = new Hashtable<String, Integer>(); // Hashtable for disease file

			logger.debug("#################Inside obesity readSNPs()#################");
			File files[] = new File(targetDataLocation).listFiles();
			for (int fileIdx = 0; fileIdx < files.length; fileIdx++) {

				String fileName = files[fileIdx].getName();
				if (!fileName.matches(fileFilterPattern)) {
					logger.debug("not matched to: " + fileFilterPattern);
					continue;
				}
				// Checks if the file Disease file is text or Gzip
				if (fileName.contains("txt")) {
					logger.debug("Disease file selected: "
							+ files[fileIdx].getAbsolutePath());
					// reads an disease file and stores into the hashtable :
					// "diseaseRsids"
					readObesityText(files[fileIdx], diseaseRsids,
							backgroundDataLocation, fileFilterPattern,
							outputFolder);		
				} else if (fileName.contains("gz")) {
					logger.debug("Disease file selected: "
							+ files[fileIdx].getAbsolutePath());
					// reads an disease file and stores into the hashtable : "diseaseRsids"
					readObesityGzip(files[fileIdx], diseaseRsids,
							backgroundDataLocation, fileFilterPattern,
							outputFolder);
				}

				diseaseRsids.clear();
			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	public void readObesityText(File fileName,
			Hashtable<String, Integer> diseaseRsids,
			String backgroundDataLocation, String fileFilterPattern,
			String outputFolder) throws NumberFormatException, IOException {
		Pattern p = Pattern.compile("(\\d+|X|Y|MT)");
		BufferedReader bfr = new BufferedReader(new FileReader(
				fileName.getAbsolutePath()));
		Matcher m = p.matcher(fileName.getName());
		if (m.find()) {
			String str;
			bfr.readLine();// skip header
			while ((str = bfr.readLine()) != null) {

				String rec[] = str.trim().split("[\\s+]");
				if (rec.length < 1) {
					continue;
				}
				diseaseRsids.put(rec[0], Integer.parseInt(m.group(0)));// filling HashTable with "Key = rsid" and "Value = chromosome number"
			}
			bfr.close();
			bfr = null;
			// writes into filtered file
			writeExtractedFiles(backgroundDataLocation, fileFilterPattern, m.group(0), outputFolder, diseaseRsids);
		}
	}

	public void readObesityGzip(File fileName,
			Hashtable<String, Integer> diseaseRsids,
			String backgroundDataLocation, String fileFilterPattern,
			String outputFolder) throws NumberFormatException, IOException {

		Pattern p = Pattern.compile("chr(\\d+|X|Y|MT)"); // TODO: need to get as input
		InputStream fileStream;
		fileStream = new FileInputStream(fileName.getAbsolutePath());
		InputStream gzipStream = new GZIPInputStream(fileStream); // Uncompressing gzip format
		Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
		BufferedReader bfr = new BufferedReader(decoder);
		Matcher m = p.matcher(fileName.getName());
		if (m.find()) {
			String str;
			bfr.readLine();// skip header
			while ((str = bfr.readLine()) != null) {
				if (str.startsWith("#")) {
					continue;
				}

				String rec[] = str.trim().split("[\\t]");
				if (rec.length < 1) {
					continue;
				}
				String chrNo = rec[0];
				long position = Long.parseLong(rec[1]);
				diseaseRsids.put("chr" + chrNo + "." + position,
						Integer.parseInt(chrNo)); // filling HashTable with "key = chromosome.position"  and "value = chrmosome number"
			}
			bfr.close();
			bfr = null;
			// writes into filtered file
			writeExtractedFiles(backgroundDataLocation, fileFilterPattern,
					m.group(0), outputFolder, diseaseRsids);
		}
	}

	public void writeExtractedFiles(String backgroundDataLocation,
			String fileFilterPattern, String matcher, String outputFolder,
			Hashtable<String, Integer> diseaseRsids) throws IOException {
		logger.debug("#################Inside 1000genome readSNPs()#################");
		File thousandgenomeFiles[] = new File(backgroundDataLocation)
		.listFiles();
		for (int fileIndx = 0; fileIndx < thousandgenomeFiles.length; fileIndx++) {
			String thousandgenomeFileName = thousandgenomeFiles[fileIndx]
					.getName();
			if (!thousandgenomeFileName.matches(fileFilterPattern)) {
				logger.debug("not matched to: " + fileFilterPattern);
				continue;
			}
			// Checking if the 1000genome filename contains "ALL.chrNo."
			if (thousandgenomeFiles[fileIndx].getName().contains(
					"ALL." + "chr" + matcher + ".")
					|| thousandgenomeFiles[fileIndx].getName().contains(
							"ALL." + matcher + ".")) {

				BufferedReader bfur;
				logger.debug("File selected 1000 genome: "
						+ thousandgenomeFiles[fileIndx].getAbsolutePath());
				// need to get as input
				Pattern p = Pattern.compile("chr(\\d+|X|Y|MT)");
				InputStream fileStream;
				fileStream = new FileInputStream(thousandgenomeFiles[fileIndx]);
				InputStream gzipStream = new GZIPInputStream(fileStream);
				Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
				bfur = new BufferedReader(decoder);
				Matcher thousandgenomem = p
						.matcher(thousandgenomeFiles[fileIndx].getName());
				if (thousandgenomem.find()) {
					String filteredIdFile = outputFolder + "/"
							+ thousandgenomem.group(0) + ".filtered.txt";
					BufferedWriter bfw = new BufferedWriter(new FileWriter(
							filteredIdFile));
					bfur.readLine();// skip header
					String str;
					while ((str = bfur.readLine()) != null) {
						if (str.startsWith("#")) {
							continue;
						}

						String rec[] = str.trim().split("[\\s+]");
						if (rec.length < 1) {
							continue;
						}
						String chrNo = rec[0];
						long position = Long.parseLong(rec[1]);
						String rsId = rec[2];
						// Checking eigther "RSID" in case of simple table or
						// "Chromosome.position" in case of VCF
						if ((diseaseRsids.containsKey(rsId) && diseaseRsids
								.contains(Integer.parseInt(chrNo)))
								|| diseaseRsids.containsKey("chr" + chrNo + "."
										+ position)) {
							// Writing to the filtered file in the format
							// "Chromosome.position #TAB# RSID"
							bfw.write("chr" + chrNo + "." + position + "\t"
									+ rsId);
							bfw.newLine();
						}
					}
					bfur.close();
					bfur = null;
					bfw.close();
					bfw = null;
				}

			}
		}
	}
}
