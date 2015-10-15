/**
 * 
 */
package edu.cmu.cs.lane.datatypes.dataset;

/**
 * @author zinman
 *
 */
public class ClinicalDataType {
	private String id;
	private String desc;
	private String type;
	
	/**
	 * 
	 */
	public ClinicalDataType(String id, String desc, String type) {
		this.id = id;
		this.desc = desc;
		this.type = type;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param name the name to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
