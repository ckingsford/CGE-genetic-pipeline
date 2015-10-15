/**
 * 
 */
package edu.cmu.cs.lane.datatypes.model;

import java.util.Date;


/**
 * @author zinman
 *
 */
public class AnalysisDetails {
	public int id;
	public String algorithmName;
	public String algParameters;
	public double cvPercent;
	public double avgAccuracy;
	public String additionalInfo;
	public String patients;
	public String targetSource;
	public String backgroundSource;
	public boolean permuted;
	public Date creationTime;
	
	public String fileShortPostfix;
	
	@Override public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(id + "\t");
		sb.append(algorithmName + "\t");
		sb.append(algParameters + "\t");
		sb.append(cvPercent + "\t");
		sb.append(avgAccuracy + "\t");
		sb.append(cvPercent + "\t");
		sb.append(additionalInfo + "\t");
		sb.append(patients + "\t");
		sb.append(targetSource + "\t");
		sb.append(backgroundSource + "\t");
		sb.append(permuted + "\t");
		sb.append(creationTime + "\t");
		sb.append(fileShortPostfix + "\t");
		return sb.toString();
	}
}
