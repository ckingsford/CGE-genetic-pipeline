/**
 * 
 */
package edu.cmu.cs.lane.datatypes.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author zinman
 *
 */
public class AnalysisSetDetails {
	public int id;
	public String name;
	public String additionalInfo;
	public Date creationTime;
	public ArrayList<AnalysisBean> analyses;
	//public String createdBy;
	//public String createdIn;
	
	@Override public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(id + "\t");
		sb.append(name + "\t");
		sb.append(additionalInfo + "\t");
		sb.append(creationTime + "\t");
		return sb.toString();
	}
}
