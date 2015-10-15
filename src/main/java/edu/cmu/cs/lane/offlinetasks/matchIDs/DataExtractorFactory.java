package edu.cmu.cs.lane.offlinetasks.matchIDs;


import java.util.Hashtable;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsInternal;

public class DataExtractorFactory {
	static private Hashtable<String, AbstractDataExtractor> factoryHash = new Hashtable<String, AbstractDataExtractor>();

	static public void initialize() {
		for (String extractor : ((OptionsInternal) OptionsFactory.getOptions("internal")).getExtractorClasses()) {
			DataExtractorFactory.add(extractor);
		}
	}

	private static void add(String extractorName) {
		try {
			Class<?> c = Class.forName(extractorName);
			AbstractDataExtractor newExtractor = (AbstractDataExtractor) c
					.newInstance();
			factoryHash.put(newExtractor.getName().toLowerCase(), newExtractor);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}
	}

	public static AbstractDataExtractor create(String type) {
		return factoryHash.get(type.toLowerCase()).create(null);
	}

	public static AbstractDataExtractor create(String type,
			String propertiesFile) {
		return factoryHash.get(type.toLowerCase()).create(propertiesFile);
	}
}
