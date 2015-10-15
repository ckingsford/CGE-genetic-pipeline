/**
 * 
 */
package edu.cmu.cs.lane.brokers.load;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.pipeline.datareader.clinicalreader.ARFFReader;
import edu.cmu.cs.lane.pipeline.datareader.clinicalreader.ClinicalTableReader;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.SimpleTableReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadClinical;

/**
 * 
 * A central place to load clinical modules
 * @author zinman
 *
 */
public class CGELoadClinicalCenter {
	static Hashtable<String, AbstractLoadClinicalModule> hashModules = new Hashtable<String, AbstractLoadClinicalModule>();
	static private String source = "";
	static private AbstractLoadClinicalModule loadModule = null;
	static Hashtable<String, String> moduleClasses = new Hashtable<String,String>();
	static private ArrayList<String> readerClasses = new ArrayList<String>();
	
	/**
	 * initialize
	 */
	static public void initialize() {
		moduleClasses.put("file", FileLoadClinicalModule.class.getCanonicalName()); //TODO: better to load the 'load' modules need to be registered dynamically
		readerClasses.add(ClinicalTableReader.class.getCanonicalName());
		readerClasses.add(ARFFReader.class.getCanonicalName());
		
		source = ((OptionsLoadClinical) OptionsFactory.getOptions("loadClinical")).getLoadClinicalSource();	
		if (source != null){
			if(moduleClasses.containsKey(source.toLowerCase())){ 
				Class<?> c;
				try {
					c = Class.forName(moduleClasses.get(source.toLowerCase()));
					loadModule = (AbstractLoadClinicalModule) c.newInstance();
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR: No class defined for LoadClinicalModule "+ source);
					e.printStackTrace();
				} catch (InstantiationException e) {
					System.out.println("ERROR: No class defined for LoadClinicalModule "+ source);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("ERROR: No class defined for LoadClinicalModule "+ source);
					e.printStackTrace();
				}
				//Instantiating the reader classes
				for (int i=0; i<readerClasses.size();i++){
					try {
						Class.forName(readerClasses.get(i)).newInstance(); //the constructor of each class will register itself
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}
	
	/**
	 * load the clinical labels
	 * @return
	 */
	static public ArrayList<SamplesLabels> loadLabels(){
		if (source != null){
			ArrayList<SamplesLabels> labelsList = new ArrayList<SamplesLabels>();
			labelsList = loadModule.load();
			return labelsList;
		}
		return null;
	}
}
