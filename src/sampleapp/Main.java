package sampleapp;


import ca.dal.apriori.inputreader.InputDataDelimiters;
import ca.dal.apriori.inputreader.InputReaderEncoder;

/**
 * The Class Main.
 */
public class Main {

	public static void main(String[] argv){
		String filepath="C:\\Users\\6910P\\Google Drive\\Dalhousie\\term_1\\data_mining\\assignment_3\\Ass3-Demo\\data1";
		
		InputReaderEncoder data= new InputReaderEncoder();
		
		try {
			data.loadAndEncodeDataFromFile(filepath,InputDataDelimiters.SPACE,true);
			data.printHeadersAndUniqueColVals();
			
		} catch (Exception e) {
			System.out.println("EXCEPTION!!");
			e.printStackTrace();
		}
	}
}
