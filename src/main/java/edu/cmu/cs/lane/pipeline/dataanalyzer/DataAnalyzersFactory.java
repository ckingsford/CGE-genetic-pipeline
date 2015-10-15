package edu.cmu.cs.lane.pipeline.dataanalyzer;

import java.util.Hashtable;

import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsInternal;

/**
 * A factory that creates new analyzers based on the definitions in the options
 * @author zinman
 *
 */
public class DataAnalyzersFactory {

	private static Hashtable<String, AbstractAnalyzer> factoryHash = new Hashtable<String, AbstractAnalyzer>();

	/**
	 * initializes internal objects
	 */
	public static void initialize() {

		for (String analyzer : ((OptionsInternal) OptionsFactory.getOptions("internal")).getAnalyzerClasses()) {
			DataAnalyzersFactory.add(analyzer);

		}
	}

	/**
	 * add additional analyzers by name
	 * @param analyzerName
	 */
	private static void add(String analyzerName) {
		try {
			Class<?> c = Class.forName(analyzerName);
			AbstractAnalyzer newAnalyzer = (AbstractAnalyzer) c.newInstance();
			factoryHash.put(newAnalyzer.getName().toLowerCase(), newAnalyzer);
			factoryHash.put(
					newAnalyzer.getName().toLowerCase(), newAnalyzer);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates a new analyzer instnace by name
	 * @param name
	 * @return
	 */
	public static AbstractAnalyzer create(String name) {
		return factoryHash.get(name.toLowerCase()).create();
	}

}
