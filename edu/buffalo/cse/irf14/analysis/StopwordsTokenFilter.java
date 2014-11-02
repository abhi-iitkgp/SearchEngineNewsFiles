package edu.buffalo.cse.irf14.analysis;

public class StopwordsTokenFilter extends TokenFilter
{
	String [] stopwords_list = {"a", "an", "are", "do", "not", "this", "is", "of"};
	
	public StopwordsTokenFilter(TokenStream stream)
	{
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException 
	{
		if(!_stream.hasNext())
		{
			return false;
		}
		
		Token token = _stream.next();
		String text = token.getTermText();
		
		for(int i=0; i < stopwords_list.length; i++)
		{
			if(text.equals(stopwords_list[i]))
			{
				_stream.remove();
				break;
			}
		}
		
		return true;
	}

	@Override
	public TokenStream getStream()
	{
		return _stream;
	}
}