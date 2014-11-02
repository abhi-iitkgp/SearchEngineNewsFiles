package edu.buffalo.cse.irf14.analysis;

public class StemmerTokenFilter extends TokenFilter
{

	public StemmerTokenFilter(TokenStream stream) 
	{
		super(stream);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException 
	{
		if(_stream.hasNext())
		{
			Token token = _stream.next();
			String text = token.getTermText();
			
			if(text.contains("@") || text.contains("#") || text.contains("2"))
			{
				return true;
			}
			
			Stemmer stemmer = new Stemmer();			
			stemmer.add(token.getTermBuffer(), token.getTermBuffer().length);
			stemmer.stem();
			String result = stemmer.toString();
			token.setTermText(result);
		}
		else
		{
			return false;
		}
				
		return true;
		// TODO Auto-generated method stub		
	}

	@Override
	public TokenStream getStream()
	{
		// TODO Auto-generated method stub
		return _stream;
	}
}
