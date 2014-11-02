package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatesTokenFilter extends TokenFilter
{

	public DatesTokenFilter(TokenStream stream)
	{
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException 
	{
		boolean is_next_token_available = false;
		
		if(_stream.hasNext())
		{
			is_next_token_available = true;
			String token_text = _stream.next().getTermText();	
			IsDate(token_text);
		}
		
		return 	is_next_token_available;
	}
	
	private boolean IsDate(String text)
	{
		boolean is_date = false;
		int i = 0;
		String formatted_date;
		String [] months = {"January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December",
				"Jan", "Feb", "Mar", "Apr", "Jun", "Aug", "Sept", "Oct", "Nov", "Dec"};
				
		if(text.compareToIgnoreCase("bc") == 0)
		{
			is_date = true;
			
			// Move back to get the year value.
			_stream.moveIndexBack();
			_stream.moveIndexBack();
			
			String token_text = _stream.next().getTermText();
			
			if(isYear(token_text))
			{
				int x = token_text.length();
						
				// appending 0's to year to make it 4 digits
				while( (4-x) > 0)
				{
					token_text = "0" + token_text;
					x++;
				}
			
				_stream.remove();
				_stream.next().setTermText("-" + token_text + "0101");
			}
		}
		
		for(i = 0; i < months.length; i++)
		{
			int moves_back = 0;
			
			if(text.equalsIgnoreCase(months[i]))
			{
				// Move back to get the date value.				
				
				if(_stream.moveIndexBack())
				{
					moves_back++;
					
					if(_stream.moveIndexBack())
					{
						moves_back++;
					}
				}	
				
				String token_day = null;
				
				if(moves_back == 2)
				{
					token_day = _stream.next().getTermText();
					moves_back--;
				}
				
				if(isDay(token_day))
				{
					token_day = padZeros(token_day, 2);					
					
					// get the month which is already read
					_stream.next();
					
					String numerical_month = getNumericalMonth(text);
					
					// get the probable year
					String token_year = _stream.next().getTermText();
					
					if(isYear(token_year))
					{
						formatted_date = token_year + numerical_month + token_day; 						
					
						_stream.remove();
						_stream.remove();
						_stream.moveIndexBack();
						_stream.next().setTermText(formatted_date);							
						break;
					}					
				}
				else
				{
					_stream.next();
					
					if(_stream.hasNext())
					{
						String day = _stream.next().getTermText();
						
						if(day.endsWith(","))
						{
							day = day.substring(0, day.length() - 1);
						}
						
						if(isDay(day))
						{
							day = padZeros(day, 2);
							
							if(_stream.hasNext())
							{
								String year = _stream.next().getTermText();
								boolean year_ends_with_punctuation = false;
								
								if(year.endsWith(",") || year.endsWith("."))
								{
									year_ends_with_punctuation = true;
								}
								
								if(isYear(year))
								{
									String numerical_month = getNumericalMonth(text);
									
									if(year_ends_with_punctuation)
									{
										formatted_date = year.substring(0, year.length() - 1) + numerical_month + day + year.substring(year.length() - 1);
									}
									else
									{
										formatted_date = year + numerical_month + day;											
									}
									
									_stream.remove();
									_stream.remove();
									_stream.moveIndexBack();
									
									_stream.next().setTermText(formatted_date);
								}
								else
								{
									String numerical_month = getNumericalMonth(text);									
									formatted_date = "1900" + numerical_month + day;										
									
									_stream.moveIndexBack();
									_stream.remove();
									
									_stream.moveIndexBack();		
									_stream.next().setTermText(formatted_date);
								}
							}						
						}
						else
						{
							_stream.moveIndexBack();
						}
					}					
				}
			}
		}
		
		if(isYear(text))
		{
			boolean next_token_available = false;
			boolean convert_num_to_date = true;
			
			if(_stream.hasNext())
			{
				next_token_available = true;				
			}						
			
			if(next_token_available)
			{
				String next_token_text = _stream.next().getTermText();				
				
				if(next_token_text.toLowerCase().endsWith("bc") || next_token_text.toLowerCase().endsWith("ad") || isMonth(next_token_text))
				{
					convert_num_to_date = false;
				}			
				
				_stream.moveIndexBack();
			}
			if(convert_num_to_date)			
			{
				formatted_date = text + "01" + "01";
				_stream.moveIndexBack();			
				_stream.next().setTermText(formatted_date);
			}
		}
		
		String regex = "(\\d+)(:)(\\d+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matches = pattern.matcher(text);
		
		if(matches.find())
		{
			boolean end_with_punctuatation_mark = false;
			
			if(text.endsWith("."))
			{
				end_with_punctuatation_mark = true;
				text = text.substring(0, text.length() - 1);
			}
			if(text.toLowerCase().endsWith("am") || text.toLowerCase().endsWith("pm"))
			{
				int start_index = 0;
				int end_index = text.indexOf(":");
				String hours = text.substring(start_index, end_index);
				start_index = end_index + 1;
				String minutes = text.substring(start_index);
				
				if(minutes.toLowerCase().endsWith("pm") || minutes.toLowerCase().endsWith("am"))
				{
					minutes = minutes.substring(0, minutes.length() - 2);
				}
				if(text.toLowerCase().endsWith("pm"))
				{
					int hours_int = Integer.parseInt(hours) + 12;
					hours = hours_int + "";
				}
				
				text = hours + ":" + minutes + ":" + "00";
			
				if(end_with_punctuatation_mark)
				{
					text = text + ".";
				}
				
				_stream.moveIndexBack();
				_stream.next().setTermText(text);			
			}
			else if(_stream.hasNext())
			{
				String next_token_text= _stream.next().getTermText();
				
				if(next_token_text.toLowerCase().startsWith("am") || next_token_text.toLowerCase().startsWith("pm"))
				{
					_stream.remove();
					_stream.moveIndexBack();
					_stream.next().setTermText(text + next_token_text);
					_stream.moveIndexBack();
				}
			}
		}

		regex = "(\\d+)(AD|BC|ad|bc)";
		pattern = Pattern.compile(regex);
		matches = pattern.matcher(text);	
		
		if(matches.find())
		{
			String period = "ad";
			boolean has_ending_punctuation = false;
			int start_index = 0;
			int end_index = text.toLowerCase().indexOf("ad");
			String final_date;
			
			if(text.endsWith("."))
			{
				has_ending_punctuation = true;
			}
			if(end_index < 0)
			{
				period = "bc";
				end_index = text.toLowerCase().indexOf("bc");
			}
			
			String year = text.substring(start_index, end_index);
			year = padZeros(year, 4);
			final_date = year + "0101";
			
			if(has_ending_punctuation)
			{
				final_date = final_date + ".";
			}
			if(period.startsWith("bc"))
			{
				final_date = "-" + final_date;
			}			
			
			_stream.moveIndexBack();
			_stream.next().setTermText(final_date);
		}
		
		regex = "(\\d+)(-)(\\d+)";
		pattern = Pattern.compile(regex);
		matches = pattern.matcher(text);		
		
		if(matches.find())
		{
			boolean has_ending_punctuation = false;
			
			if(text.endsWith("."))
			{
				has_ending_punctuation = true;
				text = text.substring(0, text.length() - 1);
			}
			
			int hypen_index = text.indexOf("-");
			
			String start_year = text.substring(0, hypen_index);
			String end_year = text.substring(hypen_index + 1);
			
			int start_year_length = start_year.length();
			int end_year_length = end_year.length();
			
			int chars_to_be_prefixed = (start_year_length) - (end_year_length);
			
			if(chars_to_be_prefixed > 0)
			{
				end_year  = start_year.substring(0, chars_to_be_prefixed) + end_year;
			}			
			
			String final_year_string = start_year + "0101" + "-" + end_year + "0101";
			
			if(has_ending_punctuation)
			{
				final_year_string += ".";
			}			
			
			_stream.moveIndexBack();
			_stream.next().setTermText(final_year_string);
		}
		
	return is_date;	
}
	
	private String padZeros(String token_text, int total_length) 
	{
		// TODO Auto-generated method stub
		int present_length = token_text.length();
		
		while((total_length - present_length) > 0)
		{
			token_text = "0" + token_text;
			present_length++;
		}	
		
		return token_text;
	}

	private String getNumericalMonth(String text) 
	{
		// TODO Auto-generated method stub
		String return_value = null;
		int i;
		String [] months = {"January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December",
				"Jan", "Feb", "Mar", "Apr", "Jun", "Aug", "Sept", "Oct", "Nov", "Dec"};
				
		for(i = 0; i < months.length; i++)
		{
			if(text.equalsIgnoreCase(months[i]))
			{
				int j = i%12;
				
				if(j < 9)
				{
					return_value =  "0" + (j+1);
				}
				else
				{
					return_value = "" + (j+1);
				}
			}
		}
		
		return return_value;
	}

	private boolean isMonth(String token_text)
	{
		String [] months = {"january", "february", "march", "april", "may", "june",
				"july", "august", "september", "october", "november", "december",
				"jan", "feb", "mar", "apr", "may", "jun", "aug", "sept", "oct", "nov", "dec"};
		
		boolean result = false;
		
		for(int i = 0; i < months.length; i++)
		{
			if(token_text.equalsIgnoreCase(months[i]))
			{
				result = true;
			}
		}
		
		return result;		
	}
	
	private boolean isYear(String token_text)
	{
		// TODO Auto-generated method stub
		boolean isYear = false;

		if(token_text.endsWith(","))
		{
			token_text = token_text.substring(0, token_text.length() - 1);
		}				
		
		try
		{
			Integer.parseInt(token_text);
			isYear = true;								
		} catch(Exception e)
		{
			
		}
		
		return isYear;
	}

	private boolean isDay(String token_text)
	{
		boolean isDay = false;
		
		if(token_text == null)
		{
			return false;
		}
		
		if(token_text.endsWith(","))
		{
			token_text = token_text.substring(0, token_text.length() - 1);
		}
		
		try
		{
			int x = Integer.parseInt(token_text);
			
			if(x <= 31)
			{
				isDay = true;
			}			
		} catch(Exception e)
		{
			
		}
		
		return isDay;
	}

	@Override
	public TokenStream getStream() 
	{
		return _stream;
	}
	
}
