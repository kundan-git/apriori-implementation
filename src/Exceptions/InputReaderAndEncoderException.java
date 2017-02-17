package Exceptions;

public class InputReaderAndEncoderException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public static String INVALID_COLUMN_LENGTH ="Insufficient columns! Check if correct delimiter is specified.";
	
	/**
	 * Instantiates a new input reader and encoder exception.
	 *
	 * @param msg the msg
	 */
	public InputReaderAndEncoderException (String msg) {
		 super (msg);
   }
}
