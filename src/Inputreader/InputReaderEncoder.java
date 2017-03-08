package Inputreader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import Interfaces.InputDataFormatter;
import Exceptions.InputReaderAndEncoderException;

public class InputReaderEncoder implements InputDataFormatter{

	/**
	 * Instantiates a new input reader encoder.
	 *
	 * @param filepath the filepath
	 * @param delimiter the delimiter
	 * @param hasHeader the has header
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InputReaderAndEncoderException 
	 */
	public InputReaderEncoder(String filepath, InputDataDelimiters delimiter, boolean hasHeader) 
			throws FileNotFoundException, IOException, InputReaderAndEncoderException {

		loadAndEncodeDataFromFile(filepath,delimiter,hasHeader);
	}

	/** 
	 * @see ca.dal.apriori.Interfaces.InputDataFormatter#getColumnsCount()
	 */
	public double getColumnsCount() {
		if(mHeaders!=null){
			mColsCnt = mHeaders.length; 
		}
		return mColsCnt;
	}

	/** 
	 * @see ca.dal.apriori.Interfaces.InputDataFormatter#getTransactionsCount()
	 */
	public double getTransactionsCount() {
		return mTxnsCnt;
	}

	/** 
	 * @see ca.dal.apriori.Interfaces.InputDataFormatter#getColHeaderToColsUnqVals()
	 */
	
	public HashMap<Integer, List<String>> getColHeaderIdxToColsUnqVals() {
		return mColHeaderIdxToColsUnqVals;
	}

	/**
	 * @see ca.dal.apriori.Interfaces.InputDataFormatter#getEncodedTransactions()
	 */
	public List<Set<Float>> getEncodedTransactions() {
		return mEncodedTxns;
	}

	
	/**
	 * Generate column header and distinct value map.
	 */
	private void generateColumnHeaderAndDistinctValueMap(){
		mEncodeHeaderToName = new HashMap<Integer, String>();
		mEncodeColsToName = new HashMap<Float, String>();
		
		if(mColHeaderIdxToColsUnqVals !=null){
			for(Integer key:mColHeaderIdxToColsUnqVals.keySet()){
				List<String> colVals=mColHeaderIdxToColsUnqVals.get(key);
				List<Float> encodedVals = mColHeaderIdxToColsEncoding.get(key);
				for(int i=0; i<colVals.size();i++){
					mEncodeColsToName.put(encodedVals.get(i), colVals.get(i));
				}
				mEncodeHeaderToName.put(key, mHeaders[key]);
			}	
		}
	}
	
	
	/**
	 * Utility function
	 * Prints the headers and unique column values.
	 */
	public void printHeadersAndUniqueColVals(){
		System.out.println("\n********** Headers and Unique Column Values*************");
		if(mColHeaderIdxToColsUnqVals !=null){
			for(Integer key:mColHeaderIdxToColsUnqVals.keySet()){
				String colsAsStr ="";
				List<String> colVals=mColHeaderIdxToColsUnqVals.get(key);
				List<Float> encodedVals = mColHeaderIdxToColsEncoding.get(key);
				for(int i=0; i<colVals.size();i++){
					colsAsStr = colsAsStr+colVals.get(i)+"("+encodedVals.get(i)+"), ";
				}
				System.out.println(mHeaders[key]+"("+key+")"+"--->"+colsAsStr);
			}	
		}
	}
	
	/**
	 * Utility Function
	 * Prints the encoded transactions.
	 */
	@SuppressWarnings("rawtypes")
	public void printEncodedTransactions(){
		
		System.out.println("\n********** Encoded Transaction*************");
		for(int idx=0;idx<mEncodedTxns.size();idx++){
			Set<Float> sets = mEncodedTxns.get(idx);
			TreeSet sortedSet = new TreeSet<Float>(sets);
			System.out.println(sortedSet);
		}
	}

	/** 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InputReaderAndEncoderException 
	 * @see ca.dal.apriori.Interfaces.InputDataFormatter#loadDataFromFile(java.lang.String)
	 */
	public void loadAndEncodeDataFromFile(
			String filepath, InputDataDelimiters delimiter, boolean hasHeader) 
					throws FileNotFoundException, IOException, InputReaderAndEncoderException {
		
		/*Set private members*/
		mFilepath = filepath;
		mDelimiter=delimiter;
		mHasHeader=hasHeader;

		/* Step 1:
		 * Get the columns headers and the distinct values present in
		 * each column. Sets mColHeaderIdxToColsUnqVals.
		 */
		setColHeadersAndDistinctColVals();
		
		/* Step 2:
		 * Build encoding for column values
		 */
		encodeColumns();
		
		//printHeadersAndUniqueColVals();
		
		/* Step 3:
		 * Encode transactions
		 */
		encodeTransactions();
		generateColumnHeaderAndDistinctValueMap();
	}
	
	
	/**
	 * Gets the encoded columns to name map.
	 *
	 * @return the encode columns to name map.
	 */
	public HashMap<Float, String> getEncodeColsToName() {
		return mEncodeColsToName;
	}

	/**
	 * Gets the encoded header to name map.
	 *
	 * @return the encoded header to name map.
	 */
	public HashMap<Integer, String> getEncodeHeaderToName() {
		return mEncodeHeaderToName;
	}
	
	/**
	 * Gets the input file path.
	 *
	 * @return the file path
	 */
	public String getFilepath() {
		return mFilepath;
	}
	
	/*
	 * Private Member Variables
	 */
	private int mTxnsCnt = -1;
	private int mColsCnt = -1;
	private List<Set<Float>> mEncodedTxns= null;
	private String[] mHeaders=null;
	private HashMap<Integer,List<String>> mColHeaderIdxToColsUnqVals=null;
	private HashMap<Integer,List<Float>> mColHeaderIdxToColsEncoding=null;
	HashMap<Integer, String> mEncodeHeaderToName = null;
	HashMap<Float, String> mEncodeColsToName = null;
	private String mFilepath=null;
	private InputDataDelimiters mDelimiter=null;
	private boolean mHasHeader=true;



	/*
	 * Private Methods
	 */


	/**
	 * Encode transactions.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void encodeTransactions() throws FileNotFoundException, IOException{

		String delim = getDelimiter(mDelimiter);
		mEncodedTxns = new ArrayList<Set<Float>>();
		BufferedReader br= new BufferedReader(new FileReader(mFilepath));
		double lineCount=0;
		for(String line; (line = br.readLine()) != null;){

			line = preProcessLine(line);
			if(mHasHeader &&(lineCount==0)){
				lineCount++;
				continue;
			}


			/* Check if the row has equal number of tokens
			 * as mentioned in header. Further, process line to 
			 * to find unique items in each column*/
			String[] colTokens = line.toLowerCase().split(delim);
			if(isValidRow(colTokens.length)){
				Set<Float> txnSet = new HashSet<Float>();
				for(int idx=0;idx<colTokens.length;idx++){
					Float encVal = getTokenEncodeValue(idx,colTokens[idx]);
					txnSet.add(encVal);
				}
				mEncodedTxns.add(txnSet);
			}
		}
		br.close();

	}
	
	
	/**
	 * Gets the token encode value.
	 *
	 * @param idx the idx
	 * @param token the token
	 * @return the token encode value
	 */
	private Float getTokenEncodeValue(int idx, String token) {
		List<String> colVals= mColHeaderIdxToColsUnqVals.get(idx);
		int tokenIdx = colVals.indexOf(token);
		return mColHeaderIdxToColsEncoding.get(idx).get(tokenIdx);
	}

	/**
	 * Encode columns
	 */
	private void encodeColumns(){
		mColHeaderIdxToColsEncoding = new HashMap<Integer,List<Float>>();
		for(Integer key:mColHeaderIdxToColsUnqVals.keySet()){
			int unqItemsCount= mColHeaderIdxToColsUnqVals.get(key).size();
			int basePower = (int) Math.ceil((float) (Math.log(unqItemsCount) / Math.log(10)));
			float step = (float) (1/Math.pow(10,basePower));
			List<Float> encodedList = new ArrayList<Float>();
			Float lastNum= (float)key;
			for(int i=0;i<mColHeaderIdxToColsUnqVals.get(key).size();i++){
				lastNum = lastNum+step;
				encodedList.add(lastNum);
			}
			mColHeaderIdxToColsEncoding.put(key, encodedList);
		}
	}


	/**
	 * Checks if a transaction row is valid.
	 *
	 * @param tokensCnt the tokens count
	 * @return true, if is valid row
	 */
	private boolean isValidRow(int tokensCnt){
		return (tokensCnt==mHeaders.length);
	}

	/**
	 * Pre process line.
	 *
	 * @param line the line
	 * @return the string
	 */
	private String preProcessLine(String line){
		if(mDelimiter==InputDataDelimiters.COMMA){
			line = line.replaceAll("\\,+", " ");	
		}else if(mDelimiter==InputDataDelimiters.SPACE){
			line = line.replaceAll("\\s+", " ");	
		}else if(mDelimiter==InputDataDelimiters.TAB){
			line = line.replaceAll("\\t+", " ");	
		}
		return line;
	}

	/**
	 * Gets the delimiter.
	 *
	 * @param enumDelim the enum delimiter
	 * @return the delimiter
	 */
	private String getDelimiter(InputDataDelimiters enumDelim){
		String delimiter="";
		if(mDelimiter==InputDataDelimiters.COMMA){
			delimiter=",";	
		}else if(mDelimiter==InputDataDelimiters.SPACE){
			delimiter=" ";	
		}else if(mDelimiter==InputDataDelimiters.TAB){
			delimiter="\t";	
		}
		return delimiter;
	}

	/**
	 * Sets the column headers and distinct column values.
	 *
	 * @param filepath the file path
	 * @param delimiter the delimiter
	 * @param hasHeader the has header
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @exception Populates mHeaders and mColHeaderIdxToColsUnqVals.
	 * @return 0 on success, -1 on failure
	 */
	private void setColHeadersAndDistinctColVals() 
			throws FileNotFoundException, IOException,InputReaderAndEncoderException{

		/* Read the file and process line by line*/
		int colCount=0;
		mColHeaderIdxToColsUnqVals = new HashMap<Integer,List<String>>();
		String delim = getDelimiter(mDelimiter);

		BufferedReader br= new BufferedReader(new FileReader(mFilepath));
		for(String line; (line = br.readLine()) != null;){

			line = preProcessLine(line);

			if((colCount==0) && mHasHeader){

				/* If first line and file has header*/
				mHeaders = line.split(delim);

				if(mHeaders.length ==1){
					br.close();
					throw new InputReaderAndEncoderException(InputReaderAndEncoderException.INVALID_COLUMN_LENGTH); 
				}

			} else if((colCount==0) && !mHasHeader){
				/* If first line and file does not have header*/
				int colCnt= line.split(delim).length;
				mHeaders = new String[colCnt];
				for(int i=0; i<colCnt;i++){
					mHeaders[i] = "col_"+i;
				}

			}else{

				/* Check if the row has equal number of tokens
				 * as mentioned in header. Further, process line to 
				 * to find unique items in each column*/
				String[] colTokens = line.toLowerCase().split(delim);


				if(isValidRow(colTokens.length)){
					for(int i=0; i<colTokens.length;i++){
						List<String> existingColVals = mColHeaderIdxToColsUnqVals.get(i);
						String token = colTokens[i].trim();

						if(existingColVals==null){
							List<String> vals= new ArrayList<String>();
							vals.add(token);
							mColHeaderIdxToColsUnqVals.put(i, vals);	
						}else if (!existingColVals.contains(token)){
							existingColVals.add(token);
							mColHeaderIdxToColsUnqVals.put(i, existingColVals);
						}
					}	
				}else if(line.equals("\n")){
					System.out.println("WARN! Skipping row as the row doesn't "
							+ "have equal tokens as defined in header.");
				}else{
					System.out.println("Error >> #columnsInRow != #columnsInHeader...");
					System.out.println("COLS_COUNT:"+colTokens.length);
					System.out.println("HEADERS_COUNT:"+mHeaders.length);
					String hdrStr="";
					for(int k=0;k<mHeaders.length;k++){hdrStr=hdrStr+mHeaders[k];System.out.println(k+"-->"+mHeaders[k]);}
					System.out.println(hdrStr);
					System.out.println(line);
				}
			}
			colCount++;
		}
		br.close();
		mTxnsCnt =colCount;
	}
	
	public HashMap<Integer, List<Float>> getColHeaderIdxToEncodedDistinctVals() {
		return mColHeaderIdxToColsEncoding;
	}

	public String[] getColumnHeaders() {
		return mHeaders;
	}
}
