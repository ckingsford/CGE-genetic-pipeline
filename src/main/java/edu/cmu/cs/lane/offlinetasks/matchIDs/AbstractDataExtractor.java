package edu.cmu.cs.lane.offlinetasks.matchIDs;

public abstract class AbstractDataExtractor {
	public abstract String getName();

	public abstract AbstractDataExtractor create(String propertiesFile);

	public abstract void read(String diseaseDataLocation,
			String thousandgenomeDataLocation, String fileFilterPattern,
			String outputFolder);

}
