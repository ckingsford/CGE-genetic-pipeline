/**
 * 
 */
package edu.cmu.cs.lane.offlinetasks;

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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
//java -Xmx8G -cp genetic-pipeline/target/classes edu.cmu.cs.lane.offlinetasks.FilterBackgroundByTarget ../data "filter\\.(\\d+)\\.txt" ../data "chr(\\d+)\\.vcf\\.gz" "../data/filtered1000Genomes/"
/**
 * @author zinman
 *
 */
public class FilterBackgroundByTarget {

	//private Hashtable<String, HashSet<String>> targetHash = new Hashtable<String, HashSet<String>>();
	static private HashSet<String> targetHash = new HashSet<String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String targetDataLocation = args[0];
		String targetFilePattern = args[1];
		String bgDataLocation = args[2];
		String bgFilePattern = args[3];
		String outputLocation = args[4];
		
		try {
			//Build target hash
			File files[] = new File(targetDataLocation).listFiles();
			for (int fileIdx = 0; fileIdx < files.length; fileIdx++) {
				String fileName = files[fileIdx].getName();
				if (fileName.matches(targetFilePattern)) {
					readTarget(files[fileIdx], targetFilePattern);
				}
			}
			
			//Filter background
			files = new File(bgDataLocation).listFiles();
			for (int fileIdx = 0; fileIdx < files.length; fileIdx++) {
				String fileName = files[fileIdx].getName();
				
				if (fileName.matches(bgFilePattern)) {
					System.out.println("BG to process: " + fileName);
					writefilteredBG(files[fileIdx], bgFilePattern,outputLocation);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static private void readTarget(File fileName,String pattern) throws NumberFormatException, IOException {
		BufferedReader bfr = new BufferedReader(new FileReader(fileName.getAbsolutePath()));
		//Pattern p = Pattern.compile(pattern);
		//Matcher m = p.matcher(fileName.getName());
		//String chr = m.group(0);
		bfr.readLine();// skip header
		String str;
		while ((str = bfr.readLine()) != null) {
			String rec[] = str.trim().split("\\t");
			if (rec.length < 1) {
				continue;
			}
			targetHash.add(rec[0]);// filling HashTable with "Key = rsid" and "Value = chromosome number"
			//System.out.println("read: " + rec[0]);
		}
		bfr.close();
		bfr = null;
	}
	
	static private void writefilteredBG(File fileName,String pattern,String outputFolder) throws NumberFormatException, IOException {
		InputStream fileStream = new FileInputStream(fileName.getAbsolutePath());
		InputStream gzipStream = new GZIPInputStream(fileStream); // Uncompressing gzip format
		Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
		BufferedReader bfr = new BufferedReader(decoder);
		pattern = "(\\d+)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(fileName.getName());
		if (m.find()){
			String chr = m.group(0);
			File file = new File(outputFolder+chr+".txt");
			file.getParentFile().mkdirs();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			
			String str;

			System.out.println("chr to write: " + chr);
			while ((str = bfr.readLine()) != null) {
				if (str.startsWith("#")) {
					bfw.write(str + "\n");
				}
				else{
					String rec[] = str.trim().split("[\\t]");
					if (rec.length < 1) {
						continue;
					}
					if(targetHash.contains(rec[2])){
						bfw.write(str + "\n");
					}
				}
			}
			bfw.close();
			bfw = null;
		}
		bfr.close();
		bfr = null;

	}

}
