package edu.buffalo.cse.irf14.analysis;

public class NumbersTokenFilter extends TokenFilter
{

	public NumbersTokenFilter(TokenStream stream)
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
			boolean result = IsNumeric(text);
			
			if(result)
			{
				String final_text = RemoveNumbers(text);
				
				if(final_text.length() == 0)
				{
					_stream.remove();
				}
				else
				{
					token.setTermText(final_text);
				}
			}
		}
		
		return checkNext;		
	}

	private String RemoveNumbers(String text)
	{
		text = text.replace(",", "");
		text = text.replace(".", "");
		
		if(text.startsWith("+") || text.startsWith("-"))
		{
			text = text.substring(1);
		}
		
		char [] numbers = text.toCharArray();
		String final_text = "";
		
		for(int i = 0;i < numbers.length; i++)
		{
			if(numbers[i] < 48 || numbers[i] > 57)
			{
				final_text += numbers[i];
			}
		}
		
		return final_text;
	}

	private boolean IsNumeric(String text) 
	{
		String [] specialChars = {"%", ",", "/"};
		boolean isFloat = false;
		
		if(text.startsWith("-"))
		{
			text = text.substring(1);
		}
		
		// removing special chars before checking if the text is a float
		for(int i = 0; i < specialChars.length; i++)
		{
			if(text.contains(specialChars[i]))
			{
				text = text.replace(specialChars[i], "");
			}
		}			
		
		try
		{
			Float.parseFloat(text);
			isFloat = true;
		}
		catch(Exception e)
		{
			isFloat = false;
		}
		
		return isFloat;
	}

	@Override
	public TokenStream getStream()
	{
		// TODO Auto-generated method stub
		return _stream;
	}

}
