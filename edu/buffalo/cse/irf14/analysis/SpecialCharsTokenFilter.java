package edu.buffalo.cse.irf14.analysis;

public class SpecialCharsTokenFilter extends TokenFilter
{

	public SpecialCharsTokenFilter(TokenStream stream) 
	{
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException
	{
		if(_stream.hasNext())
		{
			Token token = _stream.next();
			String text = token.getTermText();
			
			text= RemoveSpecialChars(text);
			
			if(text.length() != 0)
			{
				token.setTermText(text);				
			}
			else
			{
				_stream.remove();
			}
		}
		else
		{
			return false;
		}
		
		return true;
	}

	private String RemoveSpecialChars(String text)
	{
		// TODO Auto-generated method stub
		
		String [] specialChars = {"\"","(", ")", "@", "#", "~", "$", "+", "*",
				"/", "\\", "%", "=", "&", ":", ";", ">", "<", "^", "|", "_"};
		
		String [] punctuation_marks = {".", "!", "?", "'", "\\", "'", ","};		
		
		for(int i = 0; i < specialChars.length; i++)
		{
			while(text.contains(specialChars[i]))
			{
				text = text.replace(specialChars[i], "");
			}
		}	
		
		boolean startsWithPunctuation = true;
		
		while(startsWithPunctuation)
		{
			int i;
			for(i = 0; i < punctuation_marks.length; i++)
			{
				if(text.startsWith(punctuation_marks[i]))
				{
					text = text.substring(1);
					break;
				}
			}
			
			if(i == punctuation_marks.length)
			{
				break;
			}
		}
		
		int start_index = 0;
		int end_index = 0;
		
		while((end_index = text.indexOf("-", start_index)) >= 0)
		{
			String first_part, second_part;			
			
			if(end_index == start_index)
			{
				first_part = "";
				second_part = text.substring(1);
			}
			else
			{
				first_part = text.substring(0, end_index);
				second_part = text.substring(end_index + 1);				
			}
			
			if(!hasAnyNumber(first_part) && !hasAnyNumber(second_part))
			{
				text = first_part + second_part;
			}		
			
			start_index = end_index + 1;
		}
		
		return text;
	}
	
	boolean hasAnyNumber(String text)
	{
		boolean return_value = false;
		int i;
		char [] chars_in_text = text.toCharArray();
		
		for(i = 0; i < chars_in_text.length; i++)
		{
			if(chars_in_text[i] >= 48 && chars_in_text[i] <= 57)
			{
				return_value = true;
				break;
			}
		}
		
		return return_value;		
	}
	
	@Override
	public TokenStream getStream()
	{
		// TODO Auto-generated method stub
		return _stream;
	}

}
