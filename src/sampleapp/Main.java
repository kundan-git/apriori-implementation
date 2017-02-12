package sampleapp;


import java.io.FileNotFoundException;
import java.io.IOException;

import ca.dal.apriori.inputreader.InputDataDelimiters;
import ca.dal.apriori.inputreader.InputReaderAndEncoderException;
import ca.dal.apriori.inputreader.InputReaderEncoder;

/**
 * The Class Main.
 */
public class Main {

	public static void main(String[] argv) throws FileNotFoundException, IOException, InputReaderAndEncoderException{
		String filepath="C:\\Users\\6910P\\Google Drive\\Dalhousie\\term_1\\data_mining\\assignment_3\\Ass3-Demo\\data1";
		
		/*
		 * Load the data and encode it for Aprioi algorithm
		 */
		InputReaderEncoder data= new InputReaderEncoder(filepath,InputDataDelimiters.SPACE,true);
		
		data.printHeadersAndUniqueColVals();
	}
}
