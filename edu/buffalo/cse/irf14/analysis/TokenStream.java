/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author nikhillo
 * Class that represents a stream of Tokens. All {@link Analyzer} and
 * {@link TokenFilter} instances operate on this to implement their
 * behavior
 */
public class TokenStream implements Iterator<Token>
{	
	private ArrayList<Token> aList = new ArrayList<Token>();
	Token currentToken = null;
	int current_index = -1;
	
	
	/**
	 * Method that checks if there is any Token left in the stream
	 * with regards to the current pointer.
	 * DOES NOT ADVANCE THE POINTER
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext()
	{
		// TODO YOU MUST IMPLEMENT THIS			
		boolean result = false;
		int size = aList.size();
		
		if( size >= current_index + 2)
		{
			result = true;
		}
		
		return result;
	}

	/**
	 * Method to return the next Token in the stream. If a previous
	 * hasNext() call returned true, this method must return a non-null
	 * Token.
	 * If for any reason, it is called at the end of the stream, when all
	 * tokens have already been iterated, return null
	 */
	@Override
	public Token next()
	{
		Token returnValue = null;
		// TODO YOU MUST IMPLEMENT THIS			
		if(aList.size() >= current_index + 2)
		{
			current_index++;
			returnValue = aList.get(current_index);
		}

		currentToken = returnValue;
		
		return returnValue;
	}
	
	/**
	 * Method to remove the current Token from the stream.
	 * Note that "current" token refers to the Token just returned
	 * by the next method. 
	 * Must thus be NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove()
	{
		// TODO YOU MUST IMPLEMENT THIS
		if(current_index >= 0)
		{
			aList.remove(current_index);
			currentToken = null;
			current_index--;
		}
	}
	
	/**
	 * Method to reset the stream to bring the iterator back to the beginning
	 * of the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() 
	{
		//TODO : YOU MUST IMPLEMENT THIS
		current_index = -1;
	}
	
	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the iterator
	 * currently stands. After appending, the iterator position must be unchanged
	 * Of course this means if the iterator was at the end of the stream and a 
	 * new stream was appended, the iterator hasn't moved but that is no longer
	 * the end of the stream.
	 * @param stream : The stream to be appended
	 */
	public void append(TokenStream stream) 
	{		
		//TODO : YOU MUST IMPLEMENT THIS		
		if(stream != null)
		{
			stream.reset();
			while( stream.hasNext() )
			{
				aList.add(stream.next());			
			}			
		}
	}
	
	public void appendToken(Token token)
	{
		aList.add(token);
	}
	
	public Token getCurrent() {
		//TODO: YOU MUST IMPLEMENT THIS		
		return currentToken;
	}	  
	
	public boolean moveIndexBack()
	{
		if(current_index >= 0)
		{
			current_index--;	
			return true;
		}
		else
		{
			return false;			
		}
	}
	
	public int getCurrentIndexValue()
	{
		return current_index;
	}
}
