package edu.buffalo.cse.irf14.analysis;

public class CategoryAnalyser implements Analyzer{

	TokenStream _stream;
	SymbolTokenFilter stf;
	AccentsTokenFilter atf;
	SpecialCharsTokenFilter sctf;
	NumbersTokenFilter ntf;
	CapitalizationTokenFilter ctf;
	StemmerTokenFilter stem_tf;
	StopwordsTokenFilter swtf;
	
	CategoryAnalyser(TokenStream ts)
	{
		_stream = ts;
		stf = new SymbolTokenFilter(_stream);
	}
	
	@Override
	public boolean increment() throws TokenizerException 
	{		
		boolean result = false;
		
		if(_stream.hasNext())
		{
			result = true;

			stf.increment();
		}
				
		return result;
	}

	@Override
	public TokenStream getStream() 
	{		
		return _stream;
	}

}
