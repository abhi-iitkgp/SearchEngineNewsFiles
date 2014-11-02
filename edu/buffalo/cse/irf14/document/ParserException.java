/**
 * 
 */
package edu.buffalo.cse.irf14.document;

/**
 * @author nikhillo
 * Generic wrapper exception class for parsing exceptions
 */
public class ParserException extends Exception {

	/**
	 * 
	 */
	String error = null;
	
	ParserException(String text)
	{
		error = text;
	}
	
	ParserException()
	{
		
	}
	
	private static final long serialVersionUID = 4691717901217832517L;

}
