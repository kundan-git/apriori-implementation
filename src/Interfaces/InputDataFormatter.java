package Interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Inputreader.InputDataDelimiters;
import Exceptions.InputReaderAndEncoderException;

/**
 * The Interface InputDataFormatter.
 */
public interface InputDataFormatter {

	/**
	 * Gets the columns count.
	 *
	 * @return the columns count
	 */
	public double getColumnsCount();

	/**
	 * Gets the transactions count.
	 *
	 * @return the transactions count
	 */
	public double getTransactionsCount();

	/**
	 * Gets the column headers.
	 *
	 * @return the columns headers array in the order of occurence.
	 */
	public String[] getColumnHeaders();
	
	/**
	 * Gets the column header index to column's unique values.
	 *
	 * @return the map for column header to column's unique values.
	 */
	public HashMap<Integer,List<String>> getColHeaderIdxToColsUnqVals();
	
	/**
	 * Gets the column header index to column's value's encoded values.
	 *
	 * @return the map for column header to column's value's encoded values.
	 */
	public HashMap<Integer,List<Float>> getColHeaderIdxToColsEncodingVals();

	/**
	 * Gets the encoded transactions.
	 *
	 * @return the encoded transactions
	 */
	public List<Set<Float>> getEncodedTransactions();

	/**
	 * Load and encode data from file.
	 *
	 * @param filename the filename
	 * @param delimiter the delimiter
	 * @param hasHeader the has header
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void	loadAndEncodeDataFromFile(String filename,InputDataDelimiters delimiter,boolean hasHeader)  
			throws FileNotFoundException, IOException,InputReaderAndEncoderException ;

}
