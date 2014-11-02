package edu.buffalo.cse.irf14.analysis;

public class TitleFieldAnalyser implements Analyzer{

	TokenStream _stream;
	
	TitleFieldAnalyser(TokenStream stream)
	{
		_stream = stream;
	}
	
	@Override
	public boolean increment() throws TokenizerException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenStream getStream()
	{
		// TODO Auto-generated method stub
		return _stream;
	}

}
