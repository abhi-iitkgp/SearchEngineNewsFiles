package edu.buffalo.cse.irf14.analysis;

public class ContentFieldAnalyser implements Analyzer{

	TokenStream _stream;
	SymbolTokenFilter stf;
	AccentsTokenFilter atf;
	SpecialCharsTokenFilter sctf;
	NumbersTokenFilter ntf;
	CapitalizationTokenFilter ctf;
	StemmerTokenFilter stem_tf;
	StopwordsTokenFilter swtf;
	DatesTokenFilter dtf;
	
	ContentFieldAnalyser(TokenStream ts)
	{
		_stream = ts;
		stf = new SymbolTokenFilter(_stream);
		atf = new AccentsTokenFilter(_stream);
		sctf = new SpecialCharsTokenFilter(_stream);
		ntf = new NumbersTokenFilter(_stream);
		dtf = new DatesTokenFilter(_stream);
		ctf = new CapitalizationTokenFilter(_stream);		
		stem_tf= new StemmerTokenFilter(_stream);
		swtf = new StopwordsTokenFilter(_stream);
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
			_stream.moveIndexBack();		
			
			ntf.increment();
			_stream.moveIndexBack();
			
//			stem_tf.increment();
//			_stream.moveIndexBack();
			
			swtf.increment();
		}
				
		return result;
	}

	@Override
	public TokenStream getStream() 
	{		
		return _stream;
	}

}
