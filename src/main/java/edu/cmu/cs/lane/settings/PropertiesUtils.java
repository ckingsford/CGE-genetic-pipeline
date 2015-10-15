package edu.cmu.cs.lane.settings;

public class PropertiesUtils {

	public static String parseProperty(String input) {
		if (input == null)
			return "";
		int index;
		String ret = input.trim();
		if ((index = ret.indexOf('#')) > -1) {
			String t = ret.substring(0, index).trim();
			return t;
		}
		return ret;

	}

	public static String[] parseProperty(String[] input) {
		if (input == null){
			return null;
		}
		int index;
		String[] propVal = new String[input.length];
		for (int i = 0; i < input.length; i++) {
			String ret = input[i].trim();
			if ((index = ret.indexOf('#')) > -1) {
				propVal[i] = ret.substring(0, index).trim();

			}
		}
		return propVal;

	}
}
