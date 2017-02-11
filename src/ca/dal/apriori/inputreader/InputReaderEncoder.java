package ca.dal.apriori.inputreader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InputReaderEncoder implements InputDataFormatter{

	/** 
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#getColumnsCount()
	 */
	@Override
	public double getColumnsCount() {
		return mColsCnt;
	}

	/** 
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#getTransactionsCount()
	 */
	@Override
	public double getTransactionsCount() {
		return mTxnsCnt;
	}

	/** 
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#getColHeaderToColsUnqVals()
	 */
	@Override
	public HashMap<String, List<String>> getColHeaderToColsUnqVals() {
		return mColHeaderToColsUnqVals;
	}

	/**
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#getEncodedTransactions()
	 */
	@Override
	public List<Set<Float>> getEncodedTransactions() {
		return mEncodedTxns;
	}

	/**
	 * Utility function
	 * Prints the headers and unique column values.
	 */
	public void printHeadersAndUniqueColVals(){
		if(mColHeaderToColsUnqVals !=null){
			for(String key:mColHeaderToColsUnqVals.keySet()){
				String colsAsStr ="";
				List<String> colVals=mColHeaderToColsUnqVals.get(key); 
				for(int i=0; i<colVals.size();i++){
					colsAsStr = colsAsStr+","+colVals.get(i);
				}
				System.out.println(key+"--->"+colsAsStr);
			}	
		}
	}

	/** 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#loadDataFromFile(java.lang.String)
	 */
	@Override
	public void loadAndEncodeDataFromFile(
			String filepath, InputDataDelimiters delimiter, boolean hasHeader) 
					throws FileNotFoundException, IOException {

		/*Set private members*/
		mFilepath = filepath;
		mDelimiter=delimiter;
		mHasHeader=hasHeader;

		/* Step 1:
		 * Get the columns headers and the distinct values present in
		 * each column. Sets mColHeaderToColsUnqVals.
		 */
		int ret = setColHeadersAndDistinctColVals(); 



	}

	/*
	 * Private Member Variables
	 */
	private int mTxnsCnt = -1;
	private int mColsCnt = -1;
	private List<Set<Float>> mEncodedTxns= null;
	private String[] mHeaders=null;
	private HashMap<String,List<String>> mColHeaderToColsUnqVals=null;
	private String mFilepath=null;
	private InputDataDelimiters mDelimiter=null;
	private boolean mHasHeader=true;



	/*
	 * Private Methods
	 */


	/**
	 * Encode transactions.
	 */
	private void encodeTransactions(){

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
	 * @return 0 on success, -1 on failure
	 */
	private int setColHeadersAndDistinctColVals() 
			throws FileNotFoundException, IOException{

		/* Read the file and process line by line*/
		int colCount=0;
		mColHeaderToColsUnqVals = new HashMap<String,List<String>>();
		String delim = getDelimiter(mDelimiter);
		
		try(BufferedReader br= new BufferedReader(new FileReader(mFilepath))){
			for(String line; (line = br.readLine()) != null;){

				line = preProcessLine(line);
				
				if((colCount==0) && mHasHeader){
					
					/* If first line and file has header*/
					mHeaders = line.split(delim);
					if(mHeaders.length ==1){
						System.out.println("ERROR: Insufficient columns! Check if correct delimiter is specified.");
						return -1;
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
							List<String> existingColVals = mColHeaderToColsUnqVals.get(mHeaders[i]);
							String token = colTokens[i].trim();

							if(existingColVals==null){
								List<String> vals= new ArrayList<String>();
								vals.add(token);
								mColHeaderToColsUnqVals.put(mHeaders[i], vals);	
							}else if (!existingColVals.contains(token)){
								existingColVals.add(token);
								mColHeaderToColsUnqVals.put(mHeaders[i], existingColVals);
							}
						}	
					}else if(line.equals("\n")){
						System.out.println("WARN! Skipping row as the row doesn't "
								+ "have equal tokens as defined in header.");
					}
				}
				colCount++;
			}
		}
		return 0;
	}

}
