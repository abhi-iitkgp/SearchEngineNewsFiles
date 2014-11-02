/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer
{
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */	
	String delimiter = null;
	
	public Tokenizer() 
	{
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		delimiter = " ";
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) 
	{
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		delimiter = delim; 
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException 
	{
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		
		if(str == null || str.length() == 0)
		{
			throw new TokenizerException();
		}
		
		int inputLength = 0;		
		inputLength = str.length();
		
		int startIndex = 0;
		int endIndex = -1;
		String tokenData = null;
		TokenStream ts = new TokenStream();
		
		if(inputLength > 0)
		{						
			str = str.replace("\n", delimiter);
			
			while((endIndex = str.indexOf(delimiter, startIndex)) >= 0)
			{
				// if the first character in the string is delimiter, then we ignore it.
				if(endIndex == startIndex)
				{
					startIndex++;
					continue;
				}
				
				tokenData = str.substring(startIndex, endIndex).trim();				
				Token token = new Token();
				token.setTermText(tokenData);				
				
				if(tokenData.length() > 0)
				{
					ts.appendToken(token);
				}
				
				startIndex = endIndex+1;
//				System.out.println(token.getTermText());
			}
			
			tokenData = str.substring(startIndex);
			Token token = new Token();
			token.setTermText(tokenData);
			
			if(tokenData.length() > 0)
			{
				ts.appendToken(token);
			}		
		}		
		
		return ts;
	}
}
