/**
 * 
 */
package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * @author zinman
 *
 */
public class FileHelperTask {
	static public int getNumberOfLinesInFile(Reader fileReader) {
		// System.out.println("== getNumberOfLinesInFile ==");
		int count = 0;
		//LineNumberReader lnr = null;
		BufferedReader lnr = null;
		try {
			//lnr = new LineNumberReader(fileReader); //problem with this solution is that the question is whether there is a \n on the last line
			//lnr.skip(Long.MAX_VALUE); // should work for files less than 16 Exabytes
			//count = lnr.getLineNumber(); // assuming a \n on the last line
			lnr = new BufferedReader(fileReader);
			while (lnr.readLine() !=null) count++;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (lnr != null) {
				try {
					lnr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				lnr = null;
			}
		}

		return count;
	}
}
