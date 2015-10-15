package edu.cmu.cs.lane.datatypes.prediction;


/**
 * The CGEPredictionBean class represents a prediction for a specific a patient and model and for a specific clinical variable
 * or clinical state. 
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 */
public class CGEPredictionBean {
	/**
	 * The id of the clinical variable or clinical state
	 */
	public String id;
    /**
     * A general object representing a prediction value	
     */
	public String value;
	
	/**
	 * @return the id of the clinical variable/state that was predicted for
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " " + value;
	}
	
}
