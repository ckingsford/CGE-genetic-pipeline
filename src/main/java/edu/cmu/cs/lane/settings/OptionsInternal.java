/**
 * 
 */
package edu.cmu.cs.lane.settings;

import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author zinman
 *
 */
public class OptionsInternal extends AbstractOptions {
	static private String native_lib_loc;
	static private Hashtable<String, String> algotypes = new Hashtable<String, String>();
	static private String[] reader_classes;
	static private String[] extractor_classes;
	static private String[] filter_classes;
	static private String[] analyzer_classes;
	static private String[] options_classes;
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#getName()
	 */
	@Override
	public String getName() {
		return "internal";
	}

	

	
	public  void initializeInternal () throws ConfigurationException{
		
		//ClassLoader cl = OptionsInternal.class.getClassLoader();
		//PropertiesConfiguration config = new PropertiesConfiguration(cl.getResourceAsStream("system.properties"));
		//config.load(MyClass.class.getResourceAsStream("/someProps.properties"));

		PropertiesConfiguration config = new PropertiesConfiguration();
		config.load("system.properties"); //copied from genetic-pipeline/src/main/resources to genetic-pipeline/target/classes on build and initialized from there
		readParams(config);
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.lane.settings.AbstractOptions#readParams(org.apache.commons.configuration.PropertiesConfiguration)
	 */
	@Override
    public void readParams(PropertiesConfiguration config) {
		algotypes.put("lasso", "classification");
		algotypes.put("logreg", "classification");
		algotypes.put("sparselogreg", "classification");
		algotypes.put("gflasso", "regression");
		
		
		
		
		if (config.containsKey("native-lib-loc")) {
			setNativeLibLoc((config.getProperty("native-lib-loc").toString()));
		}
		
		if (config.containsKey("reader-classes")) {
			String[] strReaderClasses = config.getStringArray("reader-classes");
			if (strReaderClasses.length > 0) {
				setReaderClasses(new String[strReaderClasses.length]);
				for (int i = 0; i < strReaderClasses.length; i++) {
					setReaderClass(i, strReaderClasses[i]);
				}
			}
		}
		if (config.containsKey("extractor-classes")) {
			String[] strExtractorClasses = config
					.getStringArray("extractor-classes");
			if (strExtractorClasses.length > 0) {
				setExtractorClasses(new String[strExtractorClasses.length]);
				for (int i = 0; i < strExtractorClasses.length; i++) {
					setExtractorClass(i, strExtractorClasses[i]);
				}
			}
		}
		if (config.containsKey("filter-classes")) {
			String[] strFilterClasses = config.getStringArray("filter-classes");
			if (strFilterClasses.length > 0) {
				setFilterClasses(new String[strFilterClasses.length]);
				for (int i = 0; i < strFilterClasses.length; i++) {
					setFilterClass(i, strFilterClasses[i]);
				}
			}
		}
		if (config.containsKey("analyzer-classes")) {
			String[] strAnalyzerClasses = config
					.getStringArray("analyzer-classes");
			if (strAnalyzerClasses.length > 0) {
				setAnalyzerClasses(new String[strAnalyzerClasses.length]);
				for (int i = 0; i < strAnalyzerClasses.length; i++) {
					setAnalyzerClass(i, strAnalyzerClasses[i]);
				}
			}
		}
		if (config.containsKey("options-classes")) {
			String[] strOptionsClasses = config
					.getStringArray("options-classes");
			if (strOptionsClasses.length > 0) {
				setOptionsClasses(new String[strOptionsClasses.length]);
				for (int i = 0; i < strOptionsClasses.length; i++) {
					setOptionsClass(i, strOptionsClasses[i]);
				}
			}
		}
	}
	
	
	public String getNativeLibLoc() {
		if (native_lib_loc == null) {
			System.err.println("Warning 'native_lib_loc' is null!");
		}
		return native_lib_loc;
	}

	public void setNativeLibLoc(String native_lib_locParam) {
		native_lib_loc = native_lib_locParam;
	}
	
	public String[] getOptionsClasses() {
		if (options_classes == null) {
			System.err.println("Warning 'options_classes' is null!");
		}
		return options_classes;
	}
	public void setOptionsClasses(String[] options_classesParam) {
		options_classes = options_classesParam;
	}
	private void setOptionsClass(int i, String string) {
		options_classes[i] = string;
	}
	
	public String[] getReaderClasses() {
		if (reader_classes == null) {
			System.err.println("Warning 'reader_classes' is null!");
		}
		return reader_classes;
	}

	public void setReaderClasses(String[] reader_classesParam) {
		reader_classes = reader_classesParam;
	}

	public String[] getExtractorClasses() {
		if (extractor_classes == null) {
			System.err.println("Warning 'extractor_classes' is null!");
		}
		return extractor_classes;
	}

	public void setExtractorClasses(String[] extractor_classesParam) {
		extractor_classes = extractor_classesParam;
	}

	public String[] getFilterClasses() {
		if (filter_classes == null) {
			System.err.println("Warning 'filter_classes' is null!");
		}
		return filter_classes;
	}

	public void setFilterClasses(String[] filter_classesParam) {
		filter_classes = filter_classesParam;
	}

	public String[] getAnalyzerClasses() {
		if (analyzer_classes == null) {
			System.err.println("Warning 'analyzer_classes' is null!");
		}
		return analyzer_classes;
	}

	public void setAnalyzerClasses(String[] analyzer_classesParam) {
		analyzer_classes = analyzer_classesParam;
	}

	private void setAnalyzerClass(int i, String string) {
		analyzer_classes[i] = string;
	}

	private void setFilterClass(int i, String string) {
		filter_classes[i] = string;
	}

	private void setExtractorClass(int i, String string) {
		extractor_classes[i] = string;
	}

	private void setReaderClass(int i, String string) {
		reader_classes[i] = string;
	}








}
