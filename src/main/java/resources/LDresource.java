/**
 * 
 */
package resources;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsLD;

/**
 * @author zinman
 *
 */
public class LDresource {
	static Hashtable<Long,Hashtable<Long, Double>> LDlookup = new Hashtable<Long, Hashtable<Long, Double>>();
	static boolean isInitialized = false;
	static private String chr;
	static private double threshold;  
	static public void initialize(String requestedChr){
		if (isInitialized && chr.equalsIgnoreCase(requestedChr)) return; //this is good for sequential processing - in parallel processing - either keep LD for all chr or don't have it as a static object
		
		String inputLocation = ((OptionsLD) OptionsFactory.getOptions("LD")).getInputLocation();
		String inputPattern = ((OptionsLD) OptionsFactory.getOptions("LD")).getInputPattern();
		threshold = ((OptionsLD) OptionsFactory.getOptions("LD")).getThreshold();
		
		if (!inputLocation.equals("")){	
			chr = requestedChr;
			LDlookup.clear(); //for now info for just one chr will be kept in memory as this data may be quite large and anyway not needed beyond imputation step
			loadFromFile(inputLocation, inputPattern, requestedChr);
				
			
			
			isInitialized = true;
		}else {
			System.out.println("ERROR: LDresource is requested but no source was provided");
		}
	}
	
	static private void loadFromFile(String inputLocation, String inputPattern, String requestedChr){
		String filename = inputPattern.replaceAll("\\(.+\\)", requestedChr); //replace everything between the parenthesis
		filename = filename.replaceAll("\\\\", "");
		try {
			String str;
			BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputLocation+filename))));
			str = br.readLine(); //file format is assumed to be generated by CGE and have the following columns: snp1	snp2	distance	D_prime	D	R^2
			String[] tokens;
			while ((str = br.readLine()) != null) {
				 tokens = str.split("\t");
				 long snp1 = Long.parseLong(tokens[0]);
				 long snp2 = Long.parseLong(tokens[1]);
				 Double LD = Double.parseDouble(tokens[3]);
				 if (!LDlookup.containsKey(snp1)) LDlookup.put(snp1, new Hashtable<Long, Double>()); //this is not stored reciprocally
				 LDlookup.get(snp1).put(snp2, LD);
			}
			br.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	static public boolean isLD(long a, long b){
		return internal_isLD(a, b) || internal_isLD(b, a);
	}
	
	static private boolean internal_isLD(long a, long b){
		if(LDlookup.containsKey(a) && LDlookup.get(a).containsKey(b) && LDlookup.get(a).get(b) >= threshold){
			return true;
		}
		return false;
	}
	
	static public double getLD(long a, long b){
		if (internal_isLD(a,b)){
			return Math.abs(LDlookup.get(a).get(b));
		}
		if (internal_isLD(b,a)){
			return Math.abs(LDlookup.get(b).get(a));
		}
		return -1;
	}
	
	static public void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		loadFromFile("../data/LD/", "LD\\.(\\d+)\\.txt\\.gz", "22");
	}
}
