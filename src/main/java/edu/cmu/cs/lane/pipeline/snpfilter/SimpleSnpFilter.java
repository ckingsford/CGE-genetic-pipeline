package edu.cmu.cs.lane.pipeline.snpfilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math.stat.inference.ChiSquareTestImpl;
import org.apache.log4j.Logger;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class SimpleSnpFilter implements SnpFilter {

	private static final Logger logger = Logger.getLogger(SimpleSnpFilter.class
			.getName());

	public SimpleSnpFilter() {
	}

	@Override
	public void filterSnps(String chromosomeNumberStr) {
		// System.out.println("== filterSnps ==");
		logger.debug("processing chromosome " + chromosomeNumberStr);
		int chrNum = Integer.parseInt(chromosomeNumberStr);
		String snpFilePlaceHolder = "snpFilePlaceHolder"; //TODO:((OptionsGeneral) OptionsFactory.getOptions("columnFilters")).getSnpFilePlaceholder();
		String snpFileName = String.format(snpFilePlaceHolder ,chrNum);

		String targetInFilePath = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()+"phase2" + File.separator + "target"
				+ File.separator + snpFileName;
		String targetOutFilePath = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()+"phase3" + File.separator + "target"
				+ File.separator + snpFileName;

		String backgroundInFilePath = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()+"phase2" + File.separator + "background"
				+ File.separator
				+ snpFileName;
		String backgroundOutFilePath = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()+"phase3" + File.separator + "background"
				+ File.separator
				+ snpFileName;

		String statFileName = "getStatFilePlaceholder"; //TODO: String.format(((OptionsGeneral) OptionsFactory.getOptions("columnFilters")).getStatFilePlaceholder(),chrNum);
		String statFilePath = ((OptionsGeneral) OptionsFactory.getOptions("general")).getWorkingFolder()+"phase3" + File.separator + "stat"
				+ File.separator + statFileName;
		try {

			LinkedHashMap<String, long[]> targetSnpStats;
			LinkedHashMap<String, long[]> backgroundSnpStats;
			boolean isStatFileAvailable = false; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("columnFilters")).isStatFileAvailable()
			
			if (isStatFileAvailable == false) {
				targetSnpStats = calcSnpStat(targetInFilePath);
				backgroundSnpStats = calcSnpStat(backgroundInFilePath);
			} else {
				targetSnpStats = new LinkedHashMap<String, long[]>();
				backgroundSnpStats = new LinkedHashMap<String, long[]>();
				readSnpStat(statFilePath, targetSnpStats, backgroundSnpStats);
			}
			
			double sigCutoff = 0.5; //TODO: ((OptionsGeneral) OptionsFactory.getOptions("columnFilters")).getSigCutoff();
			List<String> snpList = calcSnpDifference(statFilePath,
					targetSnpStats, backgroundSnpStats, sigCutoff,
					!isStatFileAvailable);
			writeFilteredTabFile(targetInFilePath, targetOutFilePath, snpList);
			writeFilteredTabFile(backgroundInFilePath, backgroundOutFilePath,
					snpList);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void writeFilteredTabFile(String inFilePath, String outFilePath,
			List<String> snpList) {
		BufferedWriter bout = null;
		BufferedReader bfr = null;
		try {

			String outPath = outFilePath.substring(0,
					outFilePath.lastIndexOf(File.separator) - 1);
			File tmp = new File(outPath);
			tmp.mkdirs();

			bout = new BufferedWriter(new FileWriter(outFilePath));
			bfr = new BufferedReader(new FileReader(inFilePath));
			String str;
			String[] tokens;

			String patientsS = bfr.readLine(); // reading header line
			bout.write(patientsS + "\n");

			String snpId;
			while ((str = bfr.readLine()) != null) {
				tokens = str.split("\\s+");
				snpId = tokens[0];
				if (snpList.contains(snpId)) {
					bout.write(str + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bfr.close();
				bfr = null;
			} catch (IOException e) {
				logger.error("", e);
			}
			try {
				bout.close();
				bout = null;
			} catch (IOException e) {
				logger.error("", e);
			}
		}

	}

	private List<String> calcSnpDifference(String statFilePath,
			LinkedHashMap<String, long[]> targetSnpStats,
			LinkedHashMap<String, long[]> backgroundSnpStats, double sigCutoff,
			boolean writeToFile) throws Exception {

		ChiSquareTestImpl chiTest = new ChiSquareTestImpl();
		LinkedList<String> filteredList = new LinkedList<String>();

		File statsDir = new File(statFilePath);

		if (!statsDir.exists()) {
			if (!statsDir.exists()) {
				statsDir.getParentFile().mkdirs();
			}
		}
		BufferedWriter bout = null;
		try {
			bout = new BufferedWriter(new FileWriter(statFilePath));
			double pVal;
			for (Entry<String, long[]> entry : targetSnpStats.entrySet()) {
				String snp = entry.getKey();
				if (backgroundSnpStats.containsKey(snp)) {
					long[] obs1 = entry.getValue();
					long[] obs2 = backgroundSnpStats.get(snp);
					if (!(obs1[0] + obs2[0] == 0 || obs1[1] + obs2[1] == 0 || obs1[2]
							+ obs2[2] == 0)) {
						pVal = chiTest.chiSquareTestDataSetsComparison(obs1,
								obs2);
						if (pVal < sigCutoff) {
							// the two are different 'enough'
							filteredList.add(snp);
						}
						if (writeToFile) {
							bout.write(snp + "\t" + pVal + "\t");
							bout.write("[");
							for (int s = 0; s < obs1.length; s++) {
								bout.write((int) obs1[s] + ",");
							}
							bout.write("]\t[");
							for (int s = 0; s < obs2.length; s++) {
								bout.write((int) obs2[s] + ",");
							}
							bout.write("]");
							bout.write("\n");
						}
					}
				}
			}
		} catch (IOException ioe) {
			logger.error("", ioe);
		} finally {
			try {
				if (bout != null) {
					bout.close();
				}
			} catch (IOException ioe) {
				logger.error("", ioe);
			}
			bout = null;
		}
		if (filteredList.size() <= 0) {
			System.err
					.println("WARNING: calcSnpDifference produced zero SNPs for analysis.");
		}
		return filteredList;

	}

	private void readSnpStat(String statFileName,
			LinkedHashMap<String, long[]> targetSnpStats,
			LinkedHashMap<String, long[]> backgroundSnpStats) {
		// TODO
		throw new NotImplementedException();
	}

	private LinkedHashMap<String, long[]> calcSnpStat(String snpFilePath) {
		BufferedReader brIn = null;
		LinkedHashMap<String, long[]> stat = new LinkedHashMap<String, long[]>();
		try {
			brIn = new BufferedReader(new FileReader(snpFilePath));

			String snpId;
			String str;
			String[] tokens;
			brIn.readLine(); // reading header line
			while ((str = brIn.readLine()) != null) {
				tokens = str.split("\\s+");
				snpId = tokens[0];
				long[] stats = new long[3];
				for (int i = 1; i < tokens.length; i++) {
					// assumption no missing values anymore
					stats[Integer.parseInt(tokens[i])]++;
				}
				stat.put(snpId, stats);
			}
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			try {
				if (brIn != null) {
					brIn.close();
				}
			} catch (IOException e) {
				logger.error("", e);
			}
			brIn = null;
		}
		return stat;
	}

}
