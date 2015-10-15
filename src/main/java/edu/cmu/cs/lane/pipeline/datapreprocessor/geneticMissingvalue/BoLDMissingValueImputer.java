package edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.settings.OptionsGeneral;

public class BoLDMissingValueImputer extends AbstractGeneticMissingValueImputer {
	private static Logger logger = Logger.getLogger(BoLDMissingValueImputer.class.getName());
	private OptionsGeneral options;

	// private static final int TRACK_DISTANCE = 1000000;
	private static int TRACK_DISTANCE = 10;  //window-size equivalent
	private LDLookup ldLookup = new FakeLDLookup();
	
	@Override
	public String getName(){
		return "BoLDImputer";
	}

	/*
	public void imputeMissingValue(File inFile, File outFile,List<String> patientsToRemove) {
		BufferedReader brin = null;
		BufferedWriter brout = null;
		try {
			brin = new BufferedReader(new FileReader(inFile));
			brout = new BufferedWriter(new FileWriter(outFile));

			String line = brin.readLine(); // ignore header
			boolean[] patientsToRemoveIndex = buildPatientsToRemoveIndex(line,
					patientsToRemove);

			// write output file header
			writeHeader(patientsToRemove, line, brout);

			// read the first row
			line = brin.readLine();
			SNPRow row = parseSnpRow(line);
			SNPWindow window = new SNPWindow(row);

			// load the window
			while (window.addRow(row) == true) {
				line = brin.readLine();
				row = parseSnpRow(line);
			}

			SNPRow center = window.getCenter();
			while (center != null) {
				SNPRow newRow = imputeRow(patientsToRemoveIndex, center, window);
				writeRow(newRow, brout);
				line = brin.readLine();
				window.moveWindow(parseSnpRow(line));
				center = window.getCenter();
			}

		} catch (IOException e) {
			logger.error("", e);
		} finally {
			try {
				brin.close();
				brin = null;
			} catch (IOException e) {
				logger.error("", e);
			}
			try {
				brout.close();
				brout = null;
			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}

	private void writeRow(SNPRow newRow, BufferedWriter brout)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("chr" + newRow.chrNum + "." + newRow.position);
		for (byte val : newRow.content) {
			sb.append("\t" + val);
		}
		brout.write(sb.toString() + "\n");
		brout.flush();
	}

	private void writeHeader(List<String> patientsToRemove, String line,
			BufferedWriter brout) throws IOException {
		StringBuilder sb = new StringBuilder();
		String[] items = line.split("\\s+");
		sb.append(items[0]);
		for (int i = 1; i < items.length; i++) {
			if (patientsToRemove.contains(items[i]) == false) {
				sb.append("\t" + items[i]);
			}
		}
		brout.write(sb.toString() + "\n");
		brout.flush();
	}
	*/

	@Override
	public void setWindowSize(int size) {
		TRACK_DISTANCE = size;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.geneticMissingvalue.AbstractGeneticMissingValueImputer#imputeMissingValue(java.util.ArrayList)
	 */
	@Override
	public ArrayList<SamplesGeneticData> imputeMissingValue(ArrayList<SamplesGeneticData> data) {

		//data.get(i).data[i];
		for (int dataset = 0; dataset < data.size(); dataset++){
			int index = 0;
			String snp = data.get(dataset).getFeatureName(index);
			String[] tokens = snp.split("\\."); //assumption the id is chr.position;
			int chr = Integer.parseInt(tokens[0]);
			int pos = Integer.parseInt(tokens[1]);
			byte[] rowContent = null; //TODO: take from data
			
			SNPRow row = new SNPRow(chr, pos, rowContent);
	
			SNPWindow window = null;
			LinkedList<Byte> resultContent = new LinkedList<Byte>();
			HashMap<Integer, Double> ldSNPs = ldLookup.lookupSimilarSNPs(row.position);
			byte imputation = 0;
			double maxWeight;
			
			for (int i = 0; i < row.content.length; i++) {
				
				if (row.content[i] == options.getMissingValueSymbol()) {
					// need imputation
					double[] count = new double[3];
					if (ldSNPs != null && ldSNPs.isEmpty() == false) {
						// only for similar SNPs
						for (Entry<Integer, Double> entry : ldSNPs.entrySet()) {
							SNPRow oneRow = window.getRow(entry.getKey());
	
							if (oneRow.content[i] >= count.length) {
								continue;
							}
							// add weight
							count[oneRow.content[i]] += entry.getValue();
						}
					} else {
						// for all snps in the window
						TreeSet<SNPRow> allRows = window.getAllRows();
	
						for (SNPRow oneRow : allRows) {
							if (oneRow.content[i] >= count.length) {
								continue;
							}
							count[oneRow.content[i]] += 1;
						}
					}
					maxWeight = count[0];
					for (byte j = 0; j < count.length; j++) {
						if (count[j] > maxWeight) {
							maxWeight = count[j];
							imputation = j;
						}
					}
					resultContent.add(imputation);
				} else {
					// no need imputation
					resultContent.add(row.content[i]);
				}
			}
	
			byte[] content = new byte[resultContent.size()];
			for (int i = 0; i < content.length; i++) {
				content[i] = resultContent.get(i);
			}
		}
		return data;
	}

	private SNPRow imputeRow(boolean[] patientsToRemoveIndex, SNPRow row, SNPWindow window) {
		LinkedList<Byte> resultContent = new LinkedList<Byte>();
		HashMap<Integer, Double> ldSNPs = ldLookup.lookupSimilarSNPs(row.position);
		byte imputation = 0;
		double maxWeight;
		for (int i = 0; i < row.content.length; i++) {
			if (patientsToRemoveIndex[i] == true) {
				// do not consider
				continue;
			}
			if (row.content[i] == options.getMissingValueSymbol()) {
				// need imputation
				double[] count = new double[3];
				if (ldSNPs != null && ldSNPs.isEmpty() == false) {
					// only for similar SNPs
					for (Entry<Integer, Double> entry : ldSNPs.entrySet()) {
						SNPRow oneRow = window.getRow(entry.getKey());

						if (oneRow.content[i] >= count.length) {
							continue;
						}
						// add weight
						count[oneRow.content[i]] += entry.getValue();
					}
				} else {
					// for all snps in the window
					TreeSet<SNPRow> allRows = window.getAllRows();

					for (SNPRow oneRow : allRows) {
						if (oneRow.content[i] >= count.length) {
							continue;
						}
						count[oneRow.content[i]] += 1;
					}
				}
				maxWeight = count[0];
				for (byte j = 0; j < count.length; j++) {
					if (count[j] > maxWeight) {
						maxWeight = count[j];
						imputation = j;
					}
				}
				resultContent.add(imputation);
			} else {
				// no need imputation
				resultContent.add(row.content[i]);
			}
		}

		byte[] content = new byte[resultContent.size()];
		for (int i = 0; i < content.length; i++) {
			content[i] = resultContent.get(i);
		}

		return new SNPRow(row.chrNum, row.position, content);
	}

	/**
	 * patient index starts from 0 <br/>
	 * int[i] = true means patient with index i should be removed
	 * 
	 * @param allPatients
	 * @param patientsToRemove
	 * @return boolean[]
	 */
	private boolean[] buildPatientsToRemoveIndex(String headLine,
			List<String> patientsToRemove) {
		String[] items = headLine.split("\\s+");
		List<String> allPatients = Arrays.asList(items)
				.subList(1, items.length);
		boolean[] index = new boolean[allPatients.size()];
		for (int i = 0; i < index.length; i++) {
			index[i] = patientsToRemove.contains(allPatients.get(i));
		}
		return index;
	}

	private static SNPRow parseSnpRow(String line) {
		if (line == null || line.isEmpty()) {
			return null;
		}
		String[] items = line.split("\\s+");
		int dotPos = items[0].indexOf('.');
		int chr = Integer.parseInt(items[0].substring(3, dotPos));
		int pos = Integer.parseInt(items[0].substring(dotPos + 1));
		byte[] content = new byte[items.length - 1];
		for (int i = 1; i < items.length; i++) {
			content[i - 1] = Byte.parseByte(items[i]);
		}
		return new SNPRow(chr, pos, content);
	}

	private static class SNPRow implements Comparable<SNPRow> {

		private static final int HASH_SEED = 123;

		private int chrNum;
		private int position;
		private byte[] content;

		public SNPRow(int chr, int pos, byte[] content) {
			chrNum = chr;
			position = pos;
			this.content = content;
		}

		@Override
		public int compareTo(SNPRow o) {
			if (this.position > o.position) {
				return 1;
			} else if (this.position < o.position) {
				return -1;
			}
			return 0;

		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SNPRow)) {
				return false;
			}
			SNPRow anotherRow = (SNPRow) obj;
			return this.position == anotherRow.position ? true : false;
		}

		@Override
		public int hashCode() {
			return 37 * HASH_SEED + position;
		}
	}

	/**
	 * SNPWindow internal class
	 * @author zinman
	 *
	 */
	private static class SNPWindow {
		private TreeSet<SNPRow> window = new TreeSet<SNPRow>();
		private SNPRow centerRow;
		private TreeSet<SNPRow> buffer = new TreeSet<SNPRow>();

		public SNPWindow(SNPRow center) {
			centerRow = center;
			window.add(center);
		}

		public boolean addRow(SNPRow rowToAdd) {
			if (rowToAdd == null) {
				return false;
			}
			if (!isInWindow(rowToAdd)) {
				buffer.add(rowToAdd);
				return false;
			} else {
				window.add(rowToAdd);
				return true;
			}
		}

		public TreeSet<SNPRow> getAllRows() {
			return window;
		}

		public void moveWindow(SNPRow rowToAdd) {
			if (rowToAdd != null) {
				buffer.add(rowToAdd);
			}
			centerRow = window.higher(centerRow);
			if (centerRow != null) {
				cleanWindow();
			}
		}

		public SNPRow getCenter() {
			return centerRow;
		}

		public SNPRow getRow(int position) {
			for (SNPRow oneRow : window) {
				if (oneRow.position == position) {
					return oneRow;
				}
			}
			return null;
		}

		private boolean isInWindow(SNPRow row) {
			if (row == null) {
				return false;
			}
			return Math.abs(row.position - centerRow.position) > TRACK_DISTANCE ? false: true;
		}

		private void cleanWindow() {
			SNPRow row = window.first();
			while (!isInWindow(row)) {
				window.remove(row);
				row = window.first(); // sorted set, always test first
			}
			if (buffer.isEmpty() == false) {
				row = buffer.first();
				while (isInWindow(row)) {
					window.add(row);
					buffer.remove(row);
					if (buffer.isEmpty()) {
						row = null;
					} else {
						row = buffer.first();
					}
				}
			}
		}
	}

	private interface LDLookup {
		HashMap<Integer, Double> lookupSimilarSNPs(int snpPosition);
	}

	private class FakeLDLookup implements LDLookup {

		@Override
		public HashMap<Integer, Double> lookupSimilarSNPs(int snpPosition) {
			return null;
		}

	}

	public void setOptions(OptionsGeneral options) {
		this.options = options;
	}


}
