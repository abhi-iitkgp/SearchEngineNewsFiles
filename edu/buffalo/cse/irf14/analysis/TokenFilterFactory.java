/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	
	private static TokenFilterFactory tff = new TokenFilterFactory();
	
	private TokenFilterFactory()
	{
		
	}
	
	public static TokenFilterFactory getInstance()
	{
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		return tff;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream)
	{
		TokenFilter tokenFilter = null;
		
		if(type == TokenFilterType.ACCENT)
		{
			tokenFilter = new AccentsTokenFilter(stream);
		}
		else if(type == TokenFilterType.CAPITALIZATION)
		{
			tokenFilter = new CapitalizationTokenFilter(stream);
		}
		else if(type == TokenFilterType.DATE)
		{
			tokenFilter = new DatesTokenFilter(stream);
		}
		else if(type == TokenFilterType.NUMERIC)
		{
			tokenFilter = new NumbersTokenFilter(stream);
		}
		else if(type == TokenFilterType.SPECIALCHARS)
		{
			tokenFilter = new SpecialCharsTokenFilter(stream);
		}
		else if(type == TokenFilterType.STEMMER)
		{
			tokenFilter = new StemmerTokenFilter(stream);
		}
		else if(type == TokenFilterType.STOPWORD)
		{
			tokenFilter = new StopwordsTokenFilter(stream);
		}
		else if(type == TokenFilterType.SYMBOL)
		{
			tokenFilter = new SymbolTokenFilter(stream);
		}
		
		return tokenFilter;
	}
}
