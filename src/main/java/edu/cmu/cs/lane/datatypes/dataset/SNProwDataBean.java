package edu.cmu.cs.lane.datatypes.dataset;

import java.util.Vector;
/**
 * 
 * @author alkesh
 *
 */
public class SNProwDataBean {

    /**
     * 
     */
    String rsId;
    String fullId;
    Vector<Byte> snpVector;

    public String getFullId() {
	return fullId;
    }

    public void setFullId(String fullId) {
	this.fullId = fullId;
    }

    

    public SNProwDataBean() {

    }

    public SNProwDataBean(String rsId, String fullId, Vector<Byte> snpVector) {
	super();
	this.rsId = rsId;
	this.fullId = fullId;
	this.snpVector = snpVector;
    }

    public String getRsId() {
	return rsId;
    }

    public void setRsId(String rsId) {
	this.rsId = rsId;
    }

    public Vector<Byte> getSnpVector() {
	return snpVector;
    }

    public void setSnpVector(Vector<Byte> snpVector) {
	this.snpVector = snpVector;
    }

    @Override
    public String toString() {
	String str = "";
	if (this.fullId == null) {
	    str += this.rsId + "\t";
	} else {
	    str += this.fullId + "\t";
	}
	for (int i = 0; i < this.snpVector.size(); i++) {
	    str += snpVector.get(i).byteValue() + "\t";
	}
	return str.trim();
    }
}
