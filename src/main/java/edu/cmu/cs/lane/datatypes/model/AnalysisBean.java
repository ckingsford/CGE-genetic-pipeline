package edu.cmu.cs.lane.datatypes.model;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class AnalysisBean { 
	public ArrayList<CGEModelFeatureBean> model; //Instead of CGEModelFeatureBean - possibly should be expanded to include chromosome id or other elements
	public AnalysisDetails details;
	public ArrayList<String> chromosomes;
	
	public AnalysisBean () {
		model = new ArrayList<CGEModelFeatureBean>();
		chromosomes = new ArrayList<String>();
	}
}

