package edu.buffalo.cse.irf14.analysis;

public class CapitalizationTokenFilter extends TokenFilter
{	

	public CapitalizationTokenFilter(TokenStream stream)
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
		else
		{
			Token token = _stream.next();
			String text = token.getTermText().toLowerCase();
			token.setTermText(text);
		}
		
		return true;
	}

	@Override
	public TokenStream getStream() 
	{
		// TODO Auto-generated method stub
		return _stream;
	}
}
