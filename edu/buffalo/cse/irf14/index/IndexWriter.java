/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	
	int num_content_files = 0;
	int num_author_files = 0;
	int num_category_files = 0;		
	int num_place_files = 0;
	
	int term_id = 0;
	int place_id = 0;
	int category_id = 0;	
	int author_id;
	
	int total_files_parsed = 0;	
	String root_dir = null;
	
	HashMap<String, HashMap<String, Integer>> term_index; 
	HashMap<String,String> term_dictionary; 
	HashMap<String, HashMap<String, Integer>> author_index; 
	HashMap<String,String> author_dictionary; 
	HashMap<String, HashMap<String, Integer>> category_index; 
	HashMap<String,String> category_dictionary; 	
	HashMap<String, HashMap<String, Integer>> place_index;
	HashMap<String,String> place_dictionary; 
	HashMap<String,String> document_length_index;
	
	public IndexWriter(String indexDir)
	{
		//TODO : YOU MUST IMPLEMENT THIS
		root_dir = indexDir;
				
		term_index = new HashMap<String, HashMap<String,Integer>>(); 
		term_dictionary = new HashMap<String, String>(); 
		author_index = new HashMap<String, HashMap<String,Integer>>(); 
		author_dictionary = new HashMap<String, String>(); 
		category_index = new HashMap<String, HashMap<String,Integer>>(); 
		category_dictionary = new HashMap<String, String>(); 	
		place_index = new HashMap<String, HashMap<String,Integer>>(); 
		place_dictionary = new HashMap<String, String>(); 		
		document_length_index = new HashMap<String, String>();
	}
	
	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 * @throws TokenizerException 
	 * @throws IOException 
	 */
	public void addDocument(Document d) throws IndexerException
	{
		//TODO : YOU MUST IMPLEMENT THIS
		total_files_parsed++;
		
		int total_terms_in_doc = 0;
		
		String file_id = d.getField(FieldNames.FILEID)[0];
		
		if(document_length_index.containsKey(file_id))
		{
			return;
		}
		
		try
		{		
			Tokenizer tokenizer_title = new Tokenizer();	
			String title;
			TokenStream token_stream_title = null;
			TokenStream token_stream_place = null;
			TokenStream token_stream_date = null;
			TokenStream token_stream_content = null;
			TokenStream token_stream_category = null;
			TokenStream token_stream_author = null;
			TokenStream token_stream_author_org = null;
			
			if(d.getField(FieldNames.TITLE) != null)
			{
				title = d.getField(FieldNames.TITLE)[0];
				title = title.toLowerCase();
				total_terms_in_doc += title.split(" ").length;
				token_stream_title = tokenizer_title.consume(title);		
				Analyzer title_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.TITLE, token_stream_title);				
 				while(title_analyzer.increment());
				token_stream_title.reset();				
			}	
			
			if(d.getField(FieldNames.PLACE) != null)
			{
				num_place_files++;
				Tokenizer tokenizer_place = new Tokenizer();
				token_stream_place = tokenizer_place.consume(d.getField(FieldNames.PLACE)[0]);		
				Analyzer place_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.PLACE, token_stream_place);				
				while(place_analyzer.increment());
				token_stream_place.reset();				
			}	
			
			if(d.getField(FieldNames.NEWSDATE) != null)
			{
				Tokenizer tokenizer_date = new Tokenizer("#");
				token_stream_date = tokenizer_date.consume(d.getField(FieldNames.NEWSDATE)[0]);		
				Analyzer date_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.NEWSDATE, token_stream_date);				
				while(date_analyzer.increment());
				token_stream_date.reset();			
			}
			
			if(d.getField(FieldNames.CONTENT) != null)
			{
				num_content_files++;
				String document_content = d.getField(FieldNames.CONTENT)[0];
				document_content = document_content.toLowerCase();
				total_terms_in_doc += document_content.split(" ").length;
				Tokenizer tokenizer_content = new Tokenizer();
				token_stream_content = tokenizer_content.consume(document_content);				
				Analyzer content_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.CONTENT, token_stream_content);				
				while(content_analyzer.increment());		
				token_stream_content.reset();			
			}		
			
			if(d.getField(FieldNames.CATEGORY) != null)
			{
				num_category_files++;
				Tokenizer tokenizer_category = new Tokenizer();				
				String category = d.getField(FieldNames.CATEGORY)[0];
				
				if(category.contains("coconut-oil"))
				{
					System.out.println();
				}
				
				token_stream_category = tokenizer_category.consume(category);
				Analyzer category_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.CATEGORY, token_stream_category);
				while(category_analyzer.increment());
				token_stream_category.reset();			
			}
			
			if(d.getField(FieldNames.AUTHOR) != null)
			{
				num_author_files++;
				Tokenizer tokenizer_author = new Tokenizer("#");
				String author = d.getField(FieldNames.AUTHOR)[0];
				token_stream_author = tokenizer_author.consume(author);
				Analyzer author_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.AUTHOR, token_stream_author);
				while(author_analyzer.increment());
				token_stream_author.reset();				
			}
			if(d.getField(FieldNames.AUTHORORG) != null)
			{
				Tokenizer tokenizer_author_org = new Tokenizer("#");
				String author_org = d.getField(FieldNames.AUTHORORG)[0];
				token_stream_author_org = tokenizer_author_org.consume(author_org);
				Analyzer author_org_analyzer = AnalyzerFactory.getInstance().getAnalyzerForField(FieldNames.AUTHOR, token_stream_author_org);
				while(author_org_analyzer.increment());
				token_stream_author_org.reset();				
			}			
			
			document_length_index.put(file_id, "" + total_terms_in_doc);
			
			addToTermIndex(token_stream_content, d);
			addToTermIndex(token_stream_title, d);
			addToTermIndex(token_stream_date, d);
			addToPlaceIndex(token_stream_place, d);
			addToCategoryIndex(token_stream_category, d);		
			addToAuthorIndex(token_stream_author, d);			
			addToAuthorIndex(token_stream_author_org, d);			
		} catch(Exception e)
		{
			throw new IndexerException();
		}
	}
	
	private void WriteHashToFile() throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(root_dir + File.separator + "place_dictionary.txt"));		
		Iterator<String> terms = place_dictionary.keySet().iterator();
		
		while(terms.hasNext())
		{
			String key = terms.next();
			String value = place_dictionary.get(key);
			bw.write(key + "\r\n" + value + "\r\n");
		}
		
		bw.close();
		
		bw = new BufferedWriter(new FileWriter(root_dir + File.separator + "place_index.txt"));
		
		terms = place_index.keySet().iterator();
		
		while(terms.hasNext())
		{
			String key = terms.next();
			HashMap<String, Integer> values = place_index.get(key);
			bw.write(key + "\r\n");
			
			Iterator<String> keys_iterator = values.keySet().iterator();
			
			while(keys_iterator.hasNext())
			{
				String doc_id = keys_iterator.next();
				Integer term_frequency = values.get(doc_id);
				bw.write("<" + doc_id + "," + term_frequency + ">");				
			}
			
			bw.write("\r\n");
		}
		
		bw.close();		
	}
	private void writeToConsole(String text) 
	{
		// TODO Auto-generated method stub
//		System.out.println(text);
	}

	private void addToPostingsList(HashMap<String, Integer> postings_list, String file_id)
	{
		Integer num_occurances = (Integer)postings_list.get(file_id);
		
		if(num_occurances == null)
		{
			postings_list.put(file_id, new Integer(1));
		}
		else
		{
			postings_list.put(file_id, num_occurances + 1);
		}
	}
	
	public void addToTermIndex(TokenStream token_stream, Document d)
	{
		String file_id = d.getField(FieldNames.FILEID)[0];
		
		if(token_stream == null)
		{
			return;
		}
		
		while(token_stream.hasNext())
		{
			Token token = token_stream.next();
			String token_text = token.getTermText();
			String id = (String)term_dictionary.get(token_text);
			
			if(id == null)
			{
				id = "" + term_id;
				term_dictionary.put(token_text, id);
				term_id++;
			}
			
			HashMap<String,Integer> postings_term = (HashMap<String,Integer>)term_index.get(id);			
			
			if(postings_term == null)
			{
				postings_term = new HashMap<String,Integer>();
				term_index.put(id, postings_term);
			}
			
			addToPostingsList(postings_term, file_id);		
		}
		
		writeToConsole("DONE WITH INDEXING CONTENT IN DOCUMENT");		
	}
	
	public void addToAuthorIndex(TokenStream token_stream, Document d)
	{
		if(token_stream == null)
		{
			return;
		}
		
		while(token_stream.hasNext())
		{
			Token token = token_stream.next();
			String token_text = token.getTermText();
			String id = (String)author_dictionary.get(token_text);
			String file_id = d.getField(FieldNames.FILEID)[0];
			
			if(id == null)
			{
				id = "" + place_id;
				author_dictionary.put(token_text, id);
				author_id++;
			}
			
			HashMap<String,Integer> postings_term = (HashMap<String,Integer>)author_index.get(id);			
			
			if(postings_term == null)
			{
				postings_term = new HashMap<String,Integer>();
				author_index.put(id, postings_term);
			}
			
			addToPostingsList(postings_term, file_id);			
		}
		
		writeToConsole("DONE WITH INDEXING AUTHOR IN DOCUMENT");			
	}
	
	public void addToPlaceIndex(TokenStream token_stream, Document d)
	{
		if(token_stream == null)
		{
			return;
		}
		
		while(token_stream.hasNext())
		{
			Token token = token_stream.next();
			String token_text = token.getTermText();
			String id = (String)place_dictionary.get(token_text);
			String file_id = d.getField(FieldNames.FILEID)[0];
			
			if(id == null)
			{
				id = "" + place_id;
				place_dictionary.put(token_text, id);
				place_id++;
			}
			
			HashMap<String,Integer> postings_term = (HashMap<String,Integer>)place_index.get(id);			
			
			if(postings_term == null)
			{
				postings_term = new HashMap<String,Integer>();
				place_index.put(id, postings_term);
			}
			
			addToPostingsList(postings_term, file_id);			
		}
		
		writeToConsole("DONE WITH INDEXING PLACE IN DOCUMENT");			
	}
	
	public void addToCategoryIndex(TokenStream token_stream_category, Document d)
	{		
		if(token_stream_category == null)
		{
			return;
		}		
		
		String category = token_stream_category.next().toString();
		String file_id = d.getField(FieldNames.FILEID)[0];
		
		if(category == null)
		{
			return;
		}
		
		String id = (String)category_dictionary.get(category);
		
		if(id == null)
		{
			id = "" + category_id;
			category_dictionary.put(category, id);
			category_id++;
		}
		
		HashMap<String,Integer> postings_category = (HashMap<String,Integer>)category_index.get(id);			
		
		if(postings_category == null)
		{
			postings_category = new HashMap<String,Integer>();
			category_index.put(id, postings_category);
		}
		
		addToPostingsList(postings_category, file_id);		
	}
	
	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 * @throws IOException 
	 */
	public void close() throws IndexerException
	{		
		try
		{
			FileOutputStream fout_term_index = new FileOutputStream(root_dir + File.separator + "term_index.ser");
			ObjectOutputStream  objfile_term_index = new ObjectOutputStream(fout_term_index);
			objfile_term_index.writeObject(term_index);
			fout_term_index.close();
			objfile_term_index.close();
			
			
			FileOutputStream fout_term_dictionary = new FileOutputStream(root_dir + File.separator + "term_dictionary.ser");
			ObjectOutputStream  objfile_term_dictionary = new ObjectOutputStream(fout_term_dictionary);
			objfile_term_dictionary.writeObject(term_dictionary);
			fout_term_dictionary.close();
			objfile_term_dictionary.close();
			
			FileOutputStream fout_author_index = new FileOutputStream(root_dir + File.separator + "author_index.ser");
			ObjectOutputStream  objfile_author_index = new ObjectOutputStream(fout_author_index);
			objfile_author_index.writeObject(author_index);			
			fout_author_index.close();
			objfile_author_index.close();
			
			FileOutputStream fout_author_dictionary = new FileOutputStream(root_dir + File.separator + "author_dictionary.ser");
			ObjectOutputStream  objfile_author_dictionary = new ObjectOutputStream(fout_author_dictionary);
			objfile_author_dictionary.writeObject(author_dictionary);
			fout_author_dictionary.close();
			objfile_author_dictionary.close();
			
			FileOutputStream fout_place_index = new FileOutputStream(root_dir + File.separator + "place_index.ser");
			ObjectOutputStream  objfile_place_index = new ObjectOutputStream(fout_place_index);
			objfile_place_index.writeObject(place_index);	
			fout_place_index.close();
			objfile_place_index.close();
			
			FileOutputStream fout_place_dictionary = new FileOutputStream(root_dir + File.separator + "place_dictionary.ser");
			ObjectOutputStream  objfile_place_dictionary = new ObjectOutputStream(fout_place_dictionary);
			objfile_place_dictionary.writeObject(place_dictionary);	
			fout_place_dictionary.close();
			objfile_place_dictionary.close();
			
			FileOutputStream fout_doclength_dictionary = new FileOutputStream(root_dir + File.separator + "doc_length_index.ser");
			ObjectOutputStream  objfile_doclength_dictionary = new ObjectOutputStream(fout_doclength_dictionary);
			objfile_doclength_dictionary.writeObject(document_length_index);	
			fout_doclength_dictionary.close();
			objfile_doclength_dictionary.close();	
			
			FileOutputStream fout_category_index = new FileOutputStream(root_dir + File.separator + "category_index.ser");
			ObjectOutputStream  objfile_category_index = new ObjectOutputStream(fout_category_index);
			objfile_category_index.writeObject(category_index);	
			fout_place_index.close();
			objfile_place_index.close();
			
			FileOutputStream fout_category_dictionary = new FileOutputStream(root_dir + File.separator + "category_dictionary.ser");
			ObjectOutputStream  objfile_category_dictionary = new ObjectOutputStream(fout_category_dictionary);
			objfile_category_dictionary.writeObject(category_dictionary);	
			fout_place_dictionary.close();
			objfile_place_dictionary.close();			
		}
		catch(Exception e)
		{
			
		}
		
		try 
		{
			WriteHashToFile();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("DONE Creating indexes");
	}	
}
