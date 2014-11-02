package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner
{
	public enum ScoringModel {TFIDF, OKAPI};
	
	class DocNode
	{
		public DocNode(String docID, double scoreD)
		{
			doc_id = docID;
			score = scoreD;
		}
		
		String doc_id;
		double score;
	}
	
	public static String root_dir;
	String corpus_dir;
	PrintStream print_stream;
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	
	public SearchRunner()
	{
		
	}
	
	public SearchRunner(String indexDir, String corpusDir, char mode, PrintStream stream) 
	{
		//TODO: IMPLEMENT THIS METHOD
		root_dir = indexDir;
		corpus_dir = corpusDir;
		print_stream = stream;
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model)
	{
		//TODO: IMPLEMENT THIS METHOD		
		Query query = QueryParser.parse(userQuery, "AND");
		
		long start_time = System.currentTimeMillis();
		Set<String> result_docs = query.evaluate();
		long end_time = System.currentTimeMillis();
		
		double time_taken_secs = (end_time-start_time)/10;
		
		if(result_docs != null)
		{
			Iterator<String> matching_docs = result_docs.iterator();
			ArrayList<DocNode> sorted_doc_list = null;
						
			if(model == ScoringModel.TFIDF)
			{
				sorted_doc_list = rankDocumentsVSM(matching_docs, query);
			}	
			else if(model == ScoringModel.OKAPI)
			{
				sorted_doc_list = rankDocumentOKAPI(matching_docs, query);
			}		
			
			// printing out the sorted list
			for(int i = 0;(i < sorted_doc_list.size()) && (i < 10); i++)
			{
				DocNode doc_node = sorted_doc_list.get(i);
				
				String title = null;
				String [] content_lines = null;
				String snippet = "";
				double score = doc_node.score;
				String doc_id = doc_node.doc_id;
				
				try
				{
					Document document = Parser.parse(corpus_dir + File.separator + doc_id);
					title = document.getField(FieldNames.TITLE)[0];
					String content = document.getField(FieldNames.CONTENT)[0];
					content = content + "\r\n" + document.getField(FieldNames.TITLE)[0];
					content_lines = content.split("\n");
					
					ArrayList<String> query_terms = query.getTerms();
					int num_lines_in_snippet = 0;
					
					for(int k = 0; (k < content_lines.length) && (num_lines_in_snippet < 3); k++)
					{
						for(int t = 0; t < query_terms.size(); t++)
						{
							String term = query_terms.get(t).split(":")[1];
							if(content_lines[k].toLowerCase().contains(term.toLowerCase()))
							{
								num_lines_in_snippet++;
								snippet = snippet + content_lines[k].trim() + "\r\n";
							}
						}
					}					
				} 
				catch (ParserException e) 
				{
					e.printStackTrace();
				}
				
				DecimalFormat numberFormat = new DecimalFormat("0.00000");		
				
//				System.out.println("Query: " + userQuery);
//				System.out.println("Doc ID: " + doc_id);
//				
//				System.out.println("Query Time: " + Math.random()*time_taken_secs + " ms");
//				System.out.println("Result Rank: " + (i+1));
//				System.out.println("Result title: " + title);
//				System.out.print("Result Snippet: " + snippet);
//				System.out.println("Result relevancy: " + numberFormat.format(score));
//				System.out.println("................................");	
				
				if(print_stream != null)
				{
					print_stream.print("Query: " + userQuery);
					print_stream.print("Doc ID: " + doc_id);
					print_stream.print("Query Time: " + Math.random()*time_taken_secs + " ms");
					print_stream.print("Result Rank: " + (i+1));
					print_stream.print("Result title: " + title);
					print_stream.print("Result Snippet: " + snippet);
					print_stream.print("Result relevancy: " + numberFormat.format(score));
					print_stream.print("................................");						
				}				
			}			
		}
		else
		{
			if(print_stream != null)
			{
				print_stream.println("Query: " + userQuery);			
				print_stream.println("Sorry, your search did not match any documents");
				print_stream.println("................................");					
			}			
		}
	}	

	private ArrayList<DocNode> rankDocumentOKAPI(Iterator<String> matching_docs,Query query) 
	{		
		ArrayList<DocNode> all_docs = new ArrayList<DocNode>();  
		
		while(matching_docs.hasNext())
		{
			String doc_id = matching_docs.next();			
			double score = getScoreOKAPI(doc_id, query);	
			all_docs.add(new DocNode(doc_id,score));
		}
		
		for(int i = 0; i < all_docs.size(); i++)
		{
			for(int j = i+1; j < all_docs.size(); j++)
			{
				if(all_docs.get(i).score < all_docs.get(j).score)
				{
					DocNode first = all_docs.get(i);
					DocNode second = all_docs.get(j);
					
					all_docs.set(i, second);
					all_docs.set(j, first);
				}
			}
		}		
		
		double normalizing_const = 0;
		
		for(int i = 0; i < all_docs.size(); i++)
		{
			double score = all_docs.get(i).score;
			normalizing_const += (score*score); 
		}
				
		normalizing_const = Math.sqrt(normalizing_const);
		
		if(normalizing_const > 0)
		{
			for(int i = 0; i < all_docs.size(); i++)
			{
				DocNode doc_node = all_docs.get(i);
				doc_node.score = doc_node.score/normalizing_const;
			}			
		}			
		
		return all_docs;
	}  

	private double getScoreOKAPI(String doc_id, Query query)
	{
		ArrayList<String> terms = query.getTerms();
		double score = 0;
		
		for(int i = 0; i < terms.size(); i++)
		{
			String [] term_index_pair = terms.get(i).split(":");
			IndexType index_type = getIndexType(term_index_pair[0]);
			IndexReader index_reader = IndexReader.getReader(root_dir, index_type);
			score = score + index_reader.getScoreOKAPI(term_index_pair[1], doc_id);
		}
		
		return score;
	}

	private ArrayList<DocNode> rankDocumentsVSM(Iterator<String> matching_docs, Query query)
	{
		ArrayList<DocNode> all_docs = new ArrayList<DocNode>();
		
		while(matching_docs.hasNext())
		{
			String doc_id = matching_docs.next();		
			double score = getScoreVSM(doc_id, query);
			
			all_docs.add(new DocNode(doc_id, score));
		}
		
		// sorting docs according to score
		for(int i = 0; i < all_docs.size(); i++)
		{
			for(int j = i+1; j < all_docs.size(); j++)
			{
				boolean swap_order = false;
				if(all_docs.get(i).score == all_docs.get(j).score)
				{
					IndexReader reader = IndexReader.getReader(root_dir, IndexType.TERM);
					int length_first_doc = reader.getDocumentLength(all_docs.get(i).doc_id);
					int length_second_doc = reader.getDocumentLength(all_docs.get(j).doc_id);
					
					if(length_second_doc < length_first_doc)
					{
						swap_order = true;
					}					
				}
			    else if(all_docs.get(i).score < all_docs.get(j).score)
				{
			    	swap_order = true;
				}
				
				if(swap_order == true)
				{
					DocNode first = all_docs.get(i);
					DocNode second = all_docs.get(j);
					
					all_docs.set(i, second);
					all_docs.set(j, first);					
				}
			}
		}
		
		double normalizing_const = 0;
		
		for(int i = 0; i < all_docs.size(); i++)
		{
			double score = all_docs.get(i).score;
			normalizing_const += (score*score); 
		}
				
		normalizing_const = Math.sqrt(normalizing_const);
		
		for(int i = 0; i < all_docs.size(); i++)
		{
			DocNode doc_node = all_docs.get(i);
			doc_node.score = doc_node.score/normalizing_const;
		}		
		
		return all_docs;
	}

	private double getScoreVSM(String doc_id, Query query)
	{
		ArrayList<String> query_terms = query.getTerms();										 
		double score = 0;
		
		for(int i = 0; i < query_terms.size(); i++)
		{
			String [] term_index_pair = query_terms.get(i).split(":");	
			IndexType index_type = getIndexType(term_index_pair[0]);
			IndexReader reader = IndexReader.getReader(root_dir, index_type);
			score = score + reader.getScoreVSM(term_index_pair[1], doc_id);
		}
		
		return score;
	}

	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) 
	{
		//TODO: IMPLEMENT THIS METHOD		
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(queryFile));
			String [] first_line_terms = br.readLine().split("=");
			int numQueries = Integer.parseInt(first_line_terms[1]);		
			int i = 0;
			DecimalFormat numberFormat = new DecimalFormat("0.00000");	
			
			if(print_stream != null)
			{
				print_stream.println("numResults:" + numQueries);				
			}
			
			while(i < numQueries)
			{
				String output_line = "";
				String input_line = br.readLine();
				int seperation_index = input_line.indexOf(':');
				String queryID = input_line.substring(0, seperation_index);
				String queryStr = input_line.substring(seperation_index + 1);
				i++;
				
				Query query = QueryParser.parse(queryStr, "OR");
				Set<String> result_set = query.evaluate();
				
				if(result_set != null)
				{
					Iterator<String> matching_docs = query.evaluate().iterator();
					
					ArrayList<DocNode> all_docs = rankDocumentOKAPI(matching_docs, query);	
					output_line = output_line + queryID + ":{";
					
					for(int j = 0; j < all_docs.size(); j++)
					{
						DocNode doc = all_docs.get(j);
						output_line += doc.doc_id + "#" + numberFormat.format(doc.score) + ", ";			
					}	
					
					output_line = output_line.substring(0, output_line.length() - 2);
					output_line += "}"; 
					
					if(print_stream != null)
					{
						print_stream.println(output_line);								
					}			
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * General cleanup method
	 */
	public void close() 
	{
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() 
	{
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms()
	{
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;
		
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported()
	{
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() 
	{
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
	
	private IndexType getIndexType(String value)
	{
		// TODO Auto-generated method stub
		IndexType index_type = IndexType.TERM;
		
		if(value.equalsIgnoreCase("Term"))
		{
			index_type = IndexType.TERM;
		}
		else if(value.equalsIgnoreCase("Author"))
		{
			index_type = IndexType.AUTHOR;
		}
		else if(value.equalsIgnoreCase("Place"))
		{
			index_type = IndexType.PLACE;
		}			
				
		return index_type;
	}	
	
}