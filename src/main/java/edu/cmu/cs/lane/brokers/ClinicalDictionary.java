/**
 * 
 */
package edu.cmu.cs.lane.brokers;

import java.util.Hashtable;

import edu.cmu.cs.lane.datatypes.dataset.ClinicalDataType;

/**
 * This class provides a dictionary for various clinical descriptions that can be assigned in the system. 
 * Required in order to refer consistently to clinical variables across sites.
 * @author zinman
 *
 */
public class ClinicalDictionary {
	static Hashtable<String, ClinicalDataType> dictionary = new Hashtable<String, ClinicalDataType>();
	static public void initialize(){
		//TODO: initialize hashtable from file
		dictionary.put(getSNPIdType(), new ClinicalDataType(getSNPIdType(), "SNP ID", "String"));
	}
	static public ClinicalDataType getDataTypeInfo(String id){
		return dictionary.get(id);
	}
	static public String getSNPIdType(){
		return "SNPID";
	}
}
