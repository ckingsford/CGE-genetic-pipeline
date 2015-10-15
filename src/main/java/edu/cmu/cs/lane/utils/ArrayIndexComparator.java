/**
 * 
 */
package edu.cmu.cs.lane.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author zinman
 *
 */
public class ArrayIndexComparator implements Comparator<Integer>
{
    private final String[] array;

    public ArrayIndexComparator(String[] array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }
    
    public String[] sortByIndices(String[] input, Integer[] indices){
    	if (input.length != indices.length) return null;
    	String[] sortedArray = new String[input.length];
    	for (int i=0; i< sortedArray.length; i++){
    		sortedArray[i] = input[indices[i]];
    	}
    	return sortedArray;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
         // Autounbox from Integer to int to use as array indexes
        return array[index1].compareTo(array[index2]);
    }
    
    public static void main (String[] args){
    	String[] countries = { "France", "Spain", "Italy", "France" };
    	ArrayIndexComparator comparator = new ArrayIndexComparator(countries);
    	Integer[] indices = comparator.createIndexArray();
    	Arrays.sort(indices, comparator);
    	for (int i=0; i< indices.length; i++){
    		 System.out.println(indices[i]+"\t");
    	}
    }
}


