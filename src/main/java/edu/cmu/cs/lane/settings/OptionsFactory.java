/**
 * 
 */
package edu.cmu.cs.lane.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import edu.cmu.cs.lane.pipeline.util.FactoryUtils;

/**
 * @author zinman
 *
 */
public class OptionsFactory {
	
	static private HashMap<String, AbstractOptions> factoryHash = new HashMap<String, AbstractOptions>();
	
	static public void initializeEmpty(boolean extendedFunctionality)  {
		OptionsGeneral optionsGeneral = new OptionsGeneral();
		addOptions(optionsGeneral);
		
		if (extendedFunctionality){
			//loading internal
			OptionsInternal optionsInternal = new OptionsInternal();
			try {
				optionsInternal.initializeInternal();
				//getting the available classes for the framework
				factoryHash.put(optionsInternal.getName(), optionsInternal);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} 
		}
	}
	
	static public void initialize(String configFile, Hashtable<String, String> propertiesOverrides) throws ConfigurationException {
		initialize(configFile,propertiesOverrides,null);
	}
	static public void initialize(String configFile, Hashtable<String, String> propertiesOverrides, Hashtable<String, String> externalClasses) throws ConfigurationException { //choose which options 'modules' to include
		//loading internal
		OptionsInternal optionsInternal = new OptionsInternal();
		optionsInternal.initializeInternal(); //getting the available classes for the framework
		factoryHash.put(optionsInternal.getName(), optionsInternal);
		
		//reading the propertiesFile
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.setDelimiterParsingDisabled(true);
		config.load(configFile);
		if (propertiesOverrides != null){
			for (String key:propertiesOverrides.keySet()){
				config.setProperty(key, propertiesOverrides.get(key));
			}
		}
		//adding 'general'
		ArrayList<String> builtInOptions = new ArrayList<String>(); //TODO: should be taken from system properties
		builtInOptions.add("edu.cmu.cs.lane.settings.OptionsLD");
		builtInOptions.add("edu.cmu.cs.lane.settings.OptionsGeneral");
		builtInOptions.add("edu.cmu.cs.lane.settings.OptionsLoadGenetic");
		builtInOptions.add("edu.cmu.cs.lane.settings.OptionsLoadClinical");
		builtInOptions.add("edu.cmu.cs.lane.settings.OptionsStore");
		builtInOptions.add("edu.cmu.cs.lane.settings.OptionsPreprocessing");
		
		for (String optionsClass : builtInOptions){
			try {
				Class<?> c = Class.forName(optionsClass);
				AbstractOptions newOptions = (AbstractOptions) c.newInstance();
				String optionsName = newOptions.getName().toLowerCase();
				factoryHash.put(optionsName, newOptions);
				factoryHash.get(optionsName).readParams(config);
			} catch (Exception e) {
				System.out.println(optionsClass +" options class not found or problems in loading the relevant properties");
			}
		}

		//adding external classes
		try {
			if (externalClasses != null){
				for (String name:externalClasses.keySet()){
					factoryHash.put(name.toLowerCase(), ((AbstractOptions) FactoryUtils.classInstantiator(externalClasses.get(name))));
					factoryHash.get(name.toLowerCase()).readParams(config);
					if (((OptionsGeneral)factoryHash.get("general")).isVerbose()){
						System.out.println("loading external class: " + name);
					}
				}
			}
			
			if (optionsInternal.getOptionsClasses() != null){
				for (String r : optionsInternal.getOptionsClasses()) {//TODO: check if should be done in 'lazy' mode
						AbstractOptions optionsModule;
						optionsModule = FactoryUtils.classInstantiator(r); 
						optionsModule.readParams(config);
						factoryHash.put(optionsModule.getName().toLowerCase(), optionsModule);
						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		//based on the configFile header choose which optionsModules need to be loaded
		String modulesS = config.getString("modules","");
		String[] modules = null;
		if (modulesS != ""){
			modules = modulesS.split("[\\s,;]+");
			for (String module : modules){
				if (factoryHash.containsKey(module.toLowerCase())){
					factoryHash.get(module.toLowerCase()).readParams(config);
				}
			}
		}		
	}
	
	static public AbstractOptions getOptions(String name) {
		if (factoryHash.containsKey(name.toLowerCase())){
			return factoryHash.get(name.toLowerCase());
		}
		return null;
	}
	static public void addOptions(AbstractOptions options){ 
		factoryHash.put(options.getName().toLowerCase(), options);
	}
	
	//TODO: deal with this
	public static void lazyLoadClasses(String analyzerName){
		if (analyzerName.equalsIgnoreCase("gflasso")) {
			//initialize("algorithmicEngine/GFLassoAnalyzer.properties");
		}
		if (analyzerName.equalsIgnoreCase("shotgun")) {
			//initialize("pipeline/dataanalyzer/ShotgunAnalyzer.properties");
		}
	}
	
/*
	private boolean checkOptions() { //TODO: needs to be changed to be taken from a properties file

		if ((!this.getType().equalsIgnoreCase("regression"))
				&& (!this.getType().equalsIgnoreCase("classification"))) {
			System.out
			.println("Warning: analysis type is not defined (e.g.,'classification' or 'regression').");
			return true;
		}

		String[] algorithms = this.getAlgorithms();

		for (String algorithm : algorithms) {
			if (!algotypes.containsKey(algorithm.toLowerCase())) {
				System.err.println(algorithm + " algorithm not supported.");
				return false;
			}
			if (!algotypes.get(algorithm.toLowerCase()).equalsIgnoreCase(
					this.getType())) {
				System.err
				.println(algorithm + " does not match analysis-type.");
				return false;
			}
		}

		File dir = new File(working_folder);
		if (!dir.exists())
			if (!dir.mkdirs()) {
				System.err.println("Could not create working directory: "
						+ working_folder);
				System.exit(1);
			}
		dir = new File(output_folder);
		if (!dir.exists())
			if (!dir.mkdirs()) {
				System.err.println("Could not create output directory: "
						+ output_folder);
				System.exit(1);
			}

		return true;
	}

*/

}
