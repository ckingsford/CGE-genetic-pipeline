/**
 * 
 */
package edu.cmu.cs.lane.offlinetasks.calcLD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.*;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * @author zinman
 *
 */

//mvn2 -f genetic-pipeline/pom.xml exec:java -Dexec.Xms1G -Dexec.Xmx8G -Dexec.mainClass="edu.cmu.cs.lane.offlinetasks.calcLD.GenerateLDAssociations" -Dexec.args="../data/filtered1000Genomes/ 22.txt.gz ../data/LD/"
//mvn2 -f genetic-pipeline/pom.xml exec:java -Dexec.Xms1G -Dexec.Xmx8G -Dexec.mainClass="edu.cmu.cs.lane.offlinetasks.calcLD.GenerateLDAssociations" -Dexec.args="../data/filtered1000Genomes/ test(\\d+)\\.txt.gz ../data/LD/"

public class GenerateLDAssociations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String location = args[0];
		String pattern = args[1];
		String outputLocation = args[2];
		String currentDir = System.getProperty("user.dir");
		try {
			//Filter background
			File files[] = new File(location).listFiles();
			for (int fileIdx = 0; fileIdx < files.length; fileIdx++) {
				String fileName = files[fileIdx].getName();

				if (fileName.matches(pattern)) {
					System.out.println("File process: " + fileName);
					processFile(files[fileIdx], outputLocation);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	static private void processFile(File file,  String outputLocation){

		ArrayList<byte[]> valsQueue = new ArrayList<byte[]>();
		LinkedList<Long> positionQueue = new LinkedList<Long>();
		long windowSize = 100000;
		String chr = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
			String pattern = "(\\d+)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(file.getName());
			
			if (m.find()){
				chr = m.group(0);
				
				String str;
				String[] tokens;
				String[] varInfo = null;
				int sampleCount = 0;
				final int patientsStartColumn = 9;
				while ((str = br.readLine()) != null) {
					
					if (str.startsWith("##") ) continue;
					tokens = str.split("\t");
					if (str.startsWith("#")) {
						sampleCount = tokens.length - patientsStartColumn;
						continue;
					}
					if (tokens[1].equals("")) System.out.println(str);
					//String snp = tokens[0]+"."+tokens[1]; 
					Long newSNPpos = Long.parseLong(tokens[1]);
					positionQueue.add(newSNPpos);
	
					byte[] vals = new byte[sampleCount*2]; //because data is phased - as if doubling the counts
					byte val1 = 3;
					byte val2 = 3;
					
					for (int i=0; i<sampleCount; i++){
						String token = tokens[i+patientsStartColumn];
						String GT_phase1, GT_phase2 = null;
						if (token.contains("|")) {
							varInfo = token.split("[|]");
							String secondTerm = varInfo[1];
							GT_phase2 = secondTerm.split("[:]")[0];
						} else if (token.contains("/")) {
							varInfo = token.split("[//]");
							String secondTerm = varInfo[1];
							GT_phase2 = secondTerm;
						}
						GT_phase1 = varInfo[0];
						// System.out.println(GT_phase1+"\t"+GT_phase2);
						if (!GT_phase1.equals(".")){//missing data
							val1 = (byte) Byte.parseByte(GT_phase1);
							val1 = (byte) (val1<=1?val1:3);
						}
						if (!GT_phase2.equals(".")){//missing data
							val2 = (byte) Byte.parseByte(GT_phase2);
							val2 = (byte) (val2<=1?val2:3);
						}	
						vals[i*2] = val1;
						vals[i*2+1] = val2;
					}
					valsQueue.add(vals);
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (chr != null){
			try {
				File outfile = new File(outputLocation+"LD."+chr+".txt");
				outfile.getParentFile().mkdirs();
				BufferedWriter bfw = new BufferedWriter(new FileWriter(outfile));
				double smoothing = 0.0000001;
				bfw.write("snp1\tsnp2\tdistance\tD_prime\tD\tR^2\n");
				
				for (int i=0; i<positionQueue.size()-1; i++){
					boolean windowLimit = false;
					for (int j=i+1; j <positionQueue.size() && !windowLimit; j++){
						if (positionQueue.get(j) - positionQueue.get(i) > windowSize){
							windowLimit = true;
						}else{
							
							if (positionQueue.get(i) == 17154984){
								int a =3 ;
								a++;
							}
							
							int cmpCounts = 0;
							int A_Counts = 0;
							int B_Counts = 0;
							//calc LD
							//X11=c(A0&B0)/#cmps; X12=c(A0&B1)/#cmps; X21=c(A1&B0)/#cmps; X22=c(A1&B1)/#cmps 
							int[] xij = new int[4];
							 
							double p1 = 0;//p1=c(A0)/#A; p2=1-p1;
							double q1 = 0;//q1=c(B0)/#B; q2=1-q1;
							for (int k=0; k<valsQueue.get(i).length;k++){ //iterating over all samples
								if (valsQueue.get(i)[k] != 3 && valsQueue.get(j)[k]!=3) {//skip missing data
									cmpCounts++;
									xij[valsQueue.get(i)[k] + 2*valsQueue.get(j)[k]]++;
								}
								if (valsQueue.get(i)[k] != 3){ //calc p1
									A_Counts++;
									if (valsQueue.get(i)[k] == 0) p1++;
								}
								if (valsQueue.get(j)[k] != 3){ //calc q1
									B_Counts++;
									if (valsQueue.get(j)[k] == 0) q1++;
								}
							}
							
							double D = (((double)xij[0]*xij[3])-(xij[1]*xij[2]))/(cmpCounts*cmpCounts+ smoothing); //add minimal value to avoid dividing by 0 in an unlikely case of all missing data
							p1 = p1 / (A_Counts +smoothing); //add minimal value to avoid dividing by 0 in an unlikely case of all missing data
							q1 = q1 / (B_Counts + smoothing); //add minimal value to avoid dividing by 0 in an unlikely case of all missing data
							
							double D_prime=0;
							if (D>0) D_prime = D / Math.min(p1*(1-q1), (1-p1) *q1);
							else if(D < 0) D_prime = D / Math.max(-p1*q1, - (1-p1) *(1-q1));
							double r_2 = (D*D) / (p1 * (1-p1) * q1 * (1-q1));
							bfw.write(positionQueue.get(i)+"\t" + positionQueue.get(j)+"\t"+ (positionQueue.get(j) - positionQueue.get(i))+ "\t" + String.format("%.2f", D_prime)+"\t"+String.format("%.2f", D)+"\t"+String.format("%.2f", r_2)+"\n");
						}
					}
				}
				bfw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
} 
