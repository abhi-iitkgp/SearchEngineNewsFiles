package edu.buffalo.cse.irf14.analysis;

public class SymbolTokenFilter extends TokenFilter
{
	enum TextType
	{
		alphabetic,
		numeric,
		alphanumeric
	}
	
	
	String [] common_contractions = 
			{"let's", "won't", "shan't", "'ve", "'re", "n't", "'s", "'m", "'d", "'ll", "'em"};
	String [] common_contractions_replacements = 
			{"let us", "will not", "shall not", "have", "are", "not", "us", "am", "would", "will", "them"};
	
	String [] punctuation_marks = {".", "!", "?", "'", "\\", "'"};
	
	public SymbolTokenFilter(TokenStream stream)
	{
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException 
	{
		if(_stream.hasNext())
		{
			Token token = _stream.next();
			String term_text = token.getTermText();
			
			for(int i = 0; i < punctuation_marks.length; i++)
			{
				while(term_text.endsWith(punctuation_marks[i]))
				{
					term_text = term_text.substring(0, term_text.length()-1);
					token.setTermText(term_text);					
				}				
			}
			
			while(term_text.endsWith("!") || term_text.endsWith(".") || term_text.endsWith("?") || term_text.endsWith("'") || term_text.endsWith("-") || term_text.endsWith(",") || term_text.endsWith("\""))
			{
				term_text = term_text.substring(0, term_text.length()-1);
				token.setTermText(term_text);
			}
			
			while( term_text.startsWith("-"))
			{
				term_text = term_text.substring(1);
				token.setTermText(term_text);
			}
			
			if(term_text.endsWith("'s") || term_text.endsWith("s'"))
			{
				term_text = term_text.substring(0, term_text.length()-2);
				token.setTermText(term_text);
			}
			
			if(term_text.contains("-"))
			{
				String temp = term_text.replace("-", "");
				
				if(temp.length() == 0)
				{
					_stream.remove();			
					return true;
				}
				
				int index = term_text.indexOf("-");
				String first_part = term_text.substring(0, index);
				String last_part = term_text.substring(index+1);
				
				TextType text_type = IsAlphaNumeric(first_part + last_part);
				
				if(text_type == TextType.alphabetic)
				{
					token.setTermText(first_part + " " + last_part);					
				}				
			}
			
			term_text = token.getTermText();
			term_text = ReplaceCommonContractions(term_text);
			token.setTermText(term_text);
			
			if(term_text.contains("'"))
			{
				term_text = term_text.replace("'", "");
				token.setTermText(term_text);					
			}		
		}
		else
		{
			return false;
		}
		
		return true;		
	}

	private TextType IsAlphaNumeric(String text) 
	{
		// TODO Auto-generated method stub
		boolean isAlphabetic = false;
		boolean isNumeric = false;
		
		char [] text_array = text.toLowerCase().toCharArray();
		
		for(int i = 0; i < text_array.length;i++)
		{
			if(text_array[i] >= 48 && text_array[i] <= 57)
			{
				isNumeric = true;
				break;
			}
		}
		
		for(int i = 0; i < text_array.length;i++)
		{
			if(text_array[i] >= 97 && text_array[i] <= 122)
			{
				isAlphabetic = true;
				break;
			}
		}		
		
		if(isAlphabetic && isNumeric)
		{
			return TextType.alphanumeric;
		}
		else if(isAlphabetic)
		{
			return TextType.alphabetic;
		}
		else
		{
			return TextType.numeric;
		}
	}

	private String ReplaceCommonContractions(String term_text)
	{
		// TODO Auto-generated method stub
		for(int i = 0; i < common_contractions.length; i++)
		{
			if(term_text.endsWith(common_contractions[i]))
			{
				term_text = term_text.substring(0, term_text.length()- common_contractions[i].length());
				
				if(term_text.length() != 0)
				{
					term_text += " ";
				}
				
				term_text += common_contractions_replacements[i];
			}
		}
		
		return term_text;		
	}

	@Override
	public TokenStream getStream()
	{
		return _stream;
	}
}
