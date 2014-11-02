package edu.buffalo.cse.irf14.analysis;

public class AccentsTokenFilter extends TokenFilter
{
	String[] original_accent_strings = {"â", "à", "ô", "é", "ë", "è", "û", "ü"};
	String[] replaced_accent_strings = {"a", "a", "o", "e", "e", "e", "u", "u"};
	
	public AccentsTokenFilter(TokenStream stream)
	{
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException 
	{
		boolean checkNext = false;
		
		if(_stream.hasNext())
		{
			checkNext = true;
			Token token = _stream.next();
			String text = token.getTermText();
			
			for(int i = 0; i <  original_accent_strings.length; i++)
			{
				if(text.contains(original_accent_strings[i]))
				{
					text = text.replace(original_accent_strings[i], replaced_accent_strings[i]);					
				}
			}
			
			token.setTermText(text);
		}
		
		return checkNext;		
	}

	@Override
	public TokenStream getStream()
	{
		return _stream;
	}

}
