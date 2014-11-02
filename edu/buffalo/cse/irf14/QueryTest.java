 package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;

import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

public class QueryTest
{
	public static void main(String[] args)
	{
		PrintStream pstream = null;
		SearchRunner search_runner = new SearchRunner("D:\\IR_PROJECT_OUTPUT", "D:\\IR_PROJECT_2\\corpus_dir", 'm', pstream);
		search_runner.query("adobe", ScoringModel.OKAPI);	
		
		File file = new File("D:\\IR_PROJECT_2\\search_engine\\input.txt");
//		search_runner.query(file);
	}
}
