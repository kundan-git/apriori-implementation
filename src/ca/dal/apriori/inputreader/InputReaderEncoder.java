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
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#getColumnsCount()
	 */
	@Override
	public double getColumnsCount() {
		if(mColsCnt==-1){
			System.out.println("");
		}
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
	public HashMap<Integer, List<String>> getColHeaderIdxToColsUnqVals() {
		return mColHeaderIdxToColsUnqVals;
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InputReaderAndEncoderException 
	 * @see ca.dal.apriori.inputreader.InputDataFormatter#loadDataFromFile(java.lang.String)
	 */
	@Override
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
		encodeColumns();
		


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

		try(BufferedReader br= new BufferedReader(new FileReader(mFilepath))){
			for(String line; (line = br.readLine()) != null;){

				line = preProcessLine(line);

				if((colCount==0) && mHasHeader){

					/* If first line and file has header*/
					mHeaders = line.split(delim);
					if(mHeaders.length ==1){
						throw new InputReaderAndEncoderException("Insufficient columns! "
								+ "Check if correct delimiter is specified."); 
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
					}
				}
				colCount++;
			}
		}
	}

	@Override
	public HashMap<Integer, List<Float>> getColHeaderIdxToColsEncodingVals() {
		return mColHeaderIdxToColsEncoding;
	}

	@Override
	public String[] getColumnHeaders() {
		return mHeaders;
	}
}
