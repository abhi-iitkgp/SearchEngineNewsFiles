package edu.buffalo.cse.irf14.analysis;

public class PlaceFieldAnalyser implements Analyzer{

	TokenStream _stream;
	SymbolTokenFilter stf;
	AccentsTokenFilter atf;
	SpecialCharsTokenFilter sctf;
	NumbersTokenFilter ntf;
	CapitalizationTokenFilter ctf;
	StemmerTokenFilter stem_tf;
	StopwordsTokenFilter swtf;
	
	PlaceFieldAnalyser(TokenStream ts)
	{
		_stream = ts;
		stf = new SymbolTokenFilter(_stream);
		atf = new AccentsTokenFilter(_stream);
		sctf = new SpecialCharsTokenFilter(_stream);
		ctf = new CapitalizationTokenFilter(_stream);		
	}
	
	@Override
	public boolean increment() throws TokenizerException 
	{		
		boolean result = false;
		
		if(_stream.hasNext())
		{
			result = true;
			
			ctf.increment();
			_stream.moveIndexBack();
			
			stf.increment();
			_stream.moveIndexBack();
			
			atf.increment();
			_stream.moveIndexBack();
			
			sctf.increment();	
		}
				
		return result;
	}

	@Override
	public TokenStream getStream() 
	{		
		return _stream;
	}

}
