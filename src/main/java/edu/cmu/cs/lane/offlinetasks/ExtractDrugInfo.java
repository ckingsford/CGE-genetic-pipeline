package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ExtractDrugInfo {
  
  private static void writeLine(BufferedWriter out, String line) throws IOException {
    /*
     * 0 = externalIds
     * 1 = drugName
     * 6 = externadIds
     */
    String[] field = line.split("\t");
    
    // externalIds append field 0 to 6
    String externalIds = field[6].concat(String.format(",pharmgkb:%s", field[0]));
    
    String drugName = field[1];
    String doseGuidelines = "";
    String source = "pharmgkb";
    
    // drugName doseGuidelines externalIds source
    String output = String.format("%s\t%s\t%s\t%s\n", drugName, doseGuidelines, externalIds, source);
    
    out.write(output);
  }
  
  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 2) {
      System.out.println(String.format("Reading File: %s", args[0]));
      File drugsFile = new File(args[0]);
      
      File outputFile = new File(args[1]);
      BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
  
      Scanner fileScanner = new Scanner(drugsFile);
      fileScanner.nextLine();   // skip column labels
      while(fileScanner.hasNextLine()) {
        String line = fileScanner.nextLine();
        writeLine(out, line);
      }
      System.out.println(String.format("Writing File: %s", args[1]));
      out.close();
    }
    else {
      System.out.println("Requires two arguments, /path/to/drugs.tsv and output file");
    }
  }

}
