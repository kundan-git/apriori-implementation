package Core;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Inputreader.InputDataDelimiters;
import Inputreader.InputReaderEncoder;
import Itemsetbuilder.ItemSetBuilder;
import Exceptions.InputReaderAndEncoderException;

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
		HashMap<Integer,List<Float>> colHeaderIdxToColsEncodingVals = data.getColHeaderIdxToColsEncodingVals();
		List<Set<Float>> encodedTransactions = data.getEncodedTransactions();
		data.printHeadersAndUniqueColVals();
		//data.printEncodedTransactions();
		
		/*
		 * Get the 1-ItemSet and its support value
		 * */
		ItemSetBuilder itemSetBuilder = new ItemSetBuilder(colHeaderIdxToColsEncodingVals, 0, 0);
		List<Set<Float>> oneItemSet = itemSetBuilder.getOneItemset();
		List<Double> oneItemSupportSet = itemSetBuilder.getSupportSet(oneItemSet, encodedTransactions);
		itemSetBuilder.printItemSetAndSupport(oneItemSet, oneItemSupportSet);
		
		
	}
}
