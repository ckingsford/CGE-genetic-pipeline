package edu.cmu.cs.lane.pipeline.datareader;

import java.util.HashMap;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsInternal;
//import org.apache.log4j.Logger;
import edu.cmu.cs.lane.pipeline.util.FactoryUtils;

public class DataReaderPhaseClassFactory<T extends DataReaderPhaseFileInteractor<T>> {
	//private static final Logger logger = Logger.getLogger(DataReaderPhaseClassFactory.class);

	private HashMap<String, T> factoryHash = new HashMap<String, T>();

	public void initialize(String toLoad) { 
		if (toLoad.equalsIgnoreCase("readers")){
			for (String r : ((OptionsInternal) OptionsFactory.getOptions("internal")).getReaderClasses()) {
				T newReader;
				try {
					newReader = FactoryUtils.classInstantiator(r);
					factoryHash.put(newReader.getName().toLowerCase(), newReader);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (toLoad.equalsIgnoreCase("filters")){
			for (String f : ((OptionsInternal) OptionsFactory.getOptions("internal")).getFilterClasses()) {
				T newFilter;
				try {
					newFilter = FactoryUtils.classInstantiator(f);
					factoryHash.put(newFilter.getName().toLowerCase(), newFilter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public T create(String type) {
		String name = type.substring(type.lastIndexOf(".") + 1).toLowerCase(); 
		return factoryHash.get(name).create();
	}

}
