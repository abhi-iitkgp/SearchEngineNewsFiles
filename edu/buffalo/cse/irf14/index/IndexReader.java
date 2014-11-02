/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader 
{
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	
	private static IndexReader index_reader;
	static IndexType index_type;
	
	HashMap<String, HashMap<String, Integer>> term_index; 
	HashMap<String,String> term_dictionary; 
	HashMap<String, HashMap<String, Integer>> author_index; 
	HashMap<String,String> author_dictionary; 
	HashMap<String, HashMap<String, Integer>> category_index; 
	HashMap<String,String> category_dictionary; 	
	HashMap<String, HashMap<String, Integer>> place_index;
	HashMap<String,String> place_dictionary; 		
	HashMap<String, String []> synonyms_dictionary;
	HashMap<String, String> doc_length_index;
	
	double avg_doc_length = 0;
	
	public static IndexReader getReader(String rootDir, IndexType indexType)
	{		
		if(index_reader == null)
		{
			index_reader = new IndexReader(rootDir, indexType);
		}
		else
		{
			index_type = indexType;
		}		
		
		return index_reader;		
	}
	
	public class TermDetails
	{
		String term;
		int num_repetations;
	}	
	
	String root_dir;
	
	@SuppressWarnings("unchecked")
	public IndexReader(String indexDir, IndexType type) 
	{
		//TODO
		root_dir = indexDir;
		index_type = type;
		
		try
		{	
			FileInputStream finput_term_index = new FileInputStream(root_dir + File.separator + "term_index.ser");
			ObjectInputStream  objfile_term_index = new ObjectInputStream(finput_term_index);
			term_index = (HashMap<String, HashMap<String, Integer>>)objfile_term_index.readObject();
			finput_term_index.close();
			objfile_term_index.close();	
			
			FileInputStream finput_term_dictionary = new FileInputStream(root_dir + File.separator + "term_dictionary.ser");
			ObjectInputStream  objfile_term_dictionary = new ObjectInputStream(finput_term_dictionary);
			term_dictionary = (HashMap<String, String>)objfile_term_dictionary.readObject();
			finput_term_index.close();
			objfile_term_index.close();	
			
			FileInputStream finput_author_index = new FileInputStream(root_dir + File.separator + "author_index.ser");
			ObjectInputStream  objfile_author_index = new ObjectInputStream(finput_author_index);
			author_index = (HashMap<String, HashMap<String, Integer>>)objfile_author_index.readObject();
			finput_term_index.close();
			objfile_term_index.close();	
			
			FileInputStream finput_author_dictionary = new FileInputStream(root_dir + File.separator + "author_dictionary.ser");
			ObjectInputStream  objfile_author_dictionary = new ObjectInputStream(finput_author_dictionary);
			author_dictionary = (HashMap<String, String>)objfile_author_dictionary.readObject();
			finput_term_index.close();
			objfile_term_index.close();	
			
			FileInputStream finput_place_index = new FileInputStream(root_dir + File.separator + "place_index.ser");
			ObjectInputStream  objfile_place_index = new ObjectInputStream(finput_place_index);
			place_index = (HashMap<String, HashMap<String, Integer>>)objfile_place_index.readObject();
			finput_term_index.close();
			objfile_term_index.close();	
			
			FileInputStream finput_place_dictionary = new FileInputStream(root_dir + File.separator + "place_dictionary.ser");
			ObjectInputStream  objfile_place_dictionary = new ObjectInputStream(finput_place_dictionary);
			place_dictionary = (HashMap<String, String>)objfile_place_dictionary.readObject();
			finput_term_index.close();
			objfile_term_index.close();	
			
			FileInputStream finput_category_index = new FileInputStream(root_dir + File.separator + "category_index.ser");
			ObjectInputStream  objfile_category_index = new ObjectInputStream(finput_category_index);
			category_index = (HashMap<String, HashMap<String, Integer>>)objfile_category_index.readObject();
			finput_category_index.close();
			objfile_category_index.close();	
			
			FileInputStream finput_category_dictionary = new FileInputStream(root_dir + File.separator + "category_dictionary.ser");
			ObjectInputStream  objfile_category_dictionary = new ObjectInputStream(finput_category_dictionary);
			category_dictionary = (HashMap<String, String>)objfile_category_dictionary.readObject();
			finput_category_dictionary.close();
			objfile_category_dictionary.close();				
			
			FileInputStream finput_doclen_dictionary = new FileInputStream(root_dir + File.separator + "doc_length_index.ser");
			ObjectInputStream  objfile_doclen_dictionary = new ObjectInputStream(finput_doclen_dictionary);
			doc_length_index = (HashMap<String, String>)objfile_doclen_dictionary.readObject();
			finput_term_index.close();
			objfile_term_index.close();				
		}
		catch(Exception e)
		{
			
		}
		
		synonyms_dictionary = new HashMap<String, String[]>();
		
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("thesaures.txt")));
			String line;
			
			while((line = br.readLine()) != null)
			{
				String [] synonyms = line.split(",");
				
				for(int i = 0; i < synonyms.length; i++)
				{
					synonyms[i] = synonyms[i].trim();
				}
				
				for(int i = 0; i < synonyms.length; i++)
				{
					synonyms_dictionary.put(synonyms[i], synonyms);
				}
			}
			
			br.close();
		}
		catch(Exception e)
		{
			
		}
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 * @throws IOException 
	 */
	public int getTotalKeyTerms()
	{
		int totalKeyTerms = 0;

		
		return totalKeyTerms;
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms()
	{
		//TODO: YOU MUST IMPLEMENT THIS
		BufferedReader br = null;
		String num_files = "0";
		
		try
		{
			br = new BufferedReader(new FileReader(root_dir + File.separator + "num_files.txt"));
			num_files = br.readLine();
		} catch(Exception e)
		{
			
		}
		
		return Integer.parseInt(num_files);
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 * @throws IOException 
	 */
	public Map<String, Integer> getPostings(String term) 
	{
		//TODO:YOU MUST IMPLEMENT THIS	
		
		Map<String, String> dictionary = null;
		Map<String, HashMap<String, Integer>> index;		
		Map<String, Integer> result = null;
	
		if(index_type == IndexType.AUTHOR)
		{
			dictionary = author_dictionary;
			index = author_index;
		}
		else if(index_type == IndexType.PLACE)
		{
			dictionary = place_dictionary;
			index = place_index;
		}
		else if(index_type == IndexType.CATEGORY)
		{
			dictionary = category_dictionary;
			index = category_index;
		}				
		else
		{
			dictionary = term_dictionary;
			index = term_index;
		}
		
		term = term.toLowerCase();
		String term_id = dictionary.get(term);
		
		if(term_id != null)
		{
			result = index.get(term_id);				
		}			
		
		return result;
	}
	
	public int getDocumentLength(String docID)
	{
		String doc_length = doc_length_index.get(docID);
		return Integer.parseInt(doc_length);
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) 
	{
		//TODO : BONUS ONLY
	
		int length = terms.length;
		int i;
		
		if(length == 0)
		{
			return null;
		}
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		result = getPostings(terms[0]);
		
		for(i = 1; i < length; i++)
		{
			if(result == null)
			{
				break;
			}				
			result = intersect(result, getPostings(terms[i]));
		}
		
		return result;
	}
	
	public Map<String, Integer> intersect(Map<String, Integer> first_map, Map<String, Integer> second_map)
	{
		Map<String, Integer> result_map = new HashMap<String, Integer>();
		Integer value;
		Iterator<String> keys_first_map =  (Iterator<String>) first_map.keySet().iterator();

		while(keys_first_map.hasNext())
		{
			String key_of_map = keys_first_map.next();
			Integer value_first_index = first_map.get(key_of_map);
			Integer value_second_index;
			
			if((value_second_index = second_map.get(key_of_map)) != null)
			{
				value = value_first_index.intValue() + value_second_index.intValue();
				result_map.put(key_of_map, value);
			}			
		}
		
		if(result_map.keySet().size() == 0)
		{
			result_map = null;
		}
		
		return result_map;		
	}
	
	public double getScoreVSM(String term, String doc_id)
	{	
		if(term.startsWith("\"") && term.endsWith("\""))
		{
			term = term.substring(1, term.length() - 1);
		}
		
		term = term.toLowerCase();
		Map<String, String> dictionary = null;
		Map<String, HashMap<String, Integer>> index;		
	
		if(index_type == IndexType.AUTHOR)
		{
			dictionary = author_dictionary;
			index = author_index;
		}
		else if(index_type == IndexType.PLACE)
		{
			dictionary = place_dictionary;
			index = place_index;
		}
		else if(index_type == IndexType.CATEGORY)
		{
			dictionary = category_dictionary;
			index = category_index;
		}				
		else
		{
			dictionary = term_dictionary;
			index = term_index;
		}		
				
		String term_id = dictionary.get(term);
		double score = 0;
		int num_documents = 11500;
		
		if(term_id != null)
		{
			HashMap<String, Integer> result_docs = index.get(term_id);
			
			if(result_docs.containsKey(doc_id))
			{
				Integer term_frequency = result_docs.get(doc_id);
				int document_frequency = result_docs.size();
				score = (1 + Math.log(1+term_frequency)) * (Math.log(num_documents/document_frequency));
			}
		}
		
		return score;
	} 
	
	public double getScoreOKAPI(String term, String doc_id)
	{
		if(term.startsWith("\"") && term.endsWith("\""))
		{
			term = term.substring(1, term.length() - 1);
		}		
		
		term = term.toLowerCase();
		double k1 = 1.5;
		double b = 0.75;
		int num_documents = 10000;
		int length_doc = getDocumentLength(doc_id);
		
		Map<String, String> dictionary = null;
		Map<String, HashMap<String, Integer>> index;		
	
		if(index_type == IndexType.AUTHOR)
		{
			dictionary = author_dictionary;
			index = author_index;
		}
		else if(index_type == IndexType.PLACE)
		{
			dictionary = place_dictionary;
			index = place_index;
		}
		else if(index_type == IndexType.CATEGORY)
		{
			dictionary = category_dictionary;
			index = category_index;
		}				
		else
		{
			dictionary = term_dictionary;
			index = term_index;
		}		
		
		if(this.avg_doc_length == 0)
		{
			double total_terms = 0;
			int num_docs = doc_length_index.keySet().size();
			Iterator<String> all_doc_ids = doc_length_index.keySet().iterator();
			
			while(all_doc_ids.hasNext())
			{
				String docid_key = all_doc_ids.next();				
				String doclen = doc_length_index.get(docid_key);
				total_terms += Integer.parseInt(doclen);
			}
						
			this.avg_doc_length = total_terms/num_docs;
		}
		
		double score = 0;
		
		String term_id = dictionary.get(term);
		
		if(term_id != null)
		{
			HashMap<String, Integer> matching_docs = index.get(term_id);
			
			if(matching_docs.containsKey(doc_id))
			{
				int tf = matching_docs.get(doc_id);
				int df = matching_docs.size();
				
				double score_numerator = Math.log((1+num_documents)/df);
				score_numerator = score_numerator*(k1+1)*tf;
				
				double score_denominator = (b*length_doc)/this.avg_doc_length;
				score_denominator = score_denominator + (1-b);				
				score_denominator = k1*score_denominator + tf;
				
				score = score_numerator/score_denominator;
			}
		}
		
		return score;		
	}		
}
