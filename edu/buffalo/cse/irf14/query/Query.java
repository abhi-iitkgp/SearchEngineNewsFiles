package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.Node.NodeType;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query 
{
	/**
	 * Method to convert given parsed query into string
	 */
	
	private String query = "";
	private LinkedList<Node> query_tokens;
	private String root_dir = "";
	
	public Query(LinkedList<Node> tokens)
	{
		query_tokens = tokens;
		root_dir = SearchRunner.root_dir;
	}
	
	public ArrayList<String> getTerms()
	{
		ArrayList<String> result = new ArrayList<String>();		
		Iterator<Node> all_nodes = query_tokens.iterator();
		
		while(all_nodes.hasNext())
		{
			Node node = (Node)all_nodes.next();
			
			if(node.node_type == NodeType.clause)
			{
				result.add(node.node_value);
			}
		}
		
		return result;
	}
	
	public Set<String> evaluate()
	{
		Stack<Node> operators_stack = new Stack<Node>();
		Stack<Node> clause_stack = new Stack<Node>();
		
		for(int i = 0; i < query_tokens.size(); i++)
		{
			if(query_tokens.get(i).node_type == NodeType.operator)
			{
				operators_stack.push(query_tokens.get(i));
			}
			else if(query_tokens.get(i).node_type == NodeType.left_parenthesis)
			{ 
				operators_stack.push(query_tokens.get(i));
			}
			else if(query_tokens.get(i).node_type == NodeType.clause)
			{
				clause_stack.push(query_tokens.get(i));
			}			
			else if(query_tokens.get(i).node_type == NodeType.right_parenthesis)
			{
				Node topnode_ops_stack = operators_stack.peek();
				
				while(topnode_ops_stack.node_type != NodeType.left_parenthesis)
				{
					Node second_operand = clause_stack.pop();
					Node first_operand = clause_stack.pop();
					Node operator = operators_stack.pop();
				
					Node result = Evaluate(first_operand, second_operand, operator);
					clause_stack.push(result);
					
					topnode_ops_stack = operators_stack.peek();
				}
				
				operators_stack.pop();
			}			
		}
		
		Node result = clause_stack.pop();
		
		if(result.node_value != null && result.postings == null)
		{
			String [] first_operand_data = result.node_value.split(":");
			IndexType index_type = getIndexType(first_operand_data[0]);			
			IndexReader index_reader = IndexReader.getReader(root_dir, index_type);
			result.postings = index_reader.getPostings(result.node_value.split(":")[1]);
		}
		
		Set<String> matching_docs = null;
		
		if(result.postings != null)
		{
			matching_docs = result.postings.keySet();
		}

		return matching_docs;
	}	
	
	private Node Evaluate(Node first_operand, Node second_operand, Node operator)
	{ 
		if(first_operand == null || second_operand == null)
		{
			return null;
		}
		
//		if(first_operand.node_value.startsWith("<") && first_operand.node_value.endsWith(">"))
//		{
//			operator.node_value = "NOT";			
//			String first_operand_value = first_operand.node_value;
//			first_operand_value = first_operand_value.substring(1);
//			first_operand_value = first_operand_value.substring(0, first_operand_value.length() - 1);
//		}
		if(second_operand.node_value != null)
		{
			if(second_operand.node_value.startsWith("<") && second_operand.node_value.endsWith(">"))
			{
				operator.node_value = "NOT";			
				String second_operand_value = second_operand.node_value;
				second_operand_value = second_operand_value.substring(1);
				second_operand_value = second_operand_value.substring(0, second_operand_value.length() - 1);
				
				second_operand.node_value = second_operand_value;
			}				
		}	
		
		Node result_node = new Node();
		Map<String, Integer> first_operand_results = null;
		Map<String, Integer> second_operand_results = null;
		
		if(first_operand.postings == null)
		{
			if(first_operand.node_value != null)
			{
				String [] first_operand_data = first_operand.node_value.split(":");
				
				if(first_operand_data[1].startsWith("\"") && first_operand_data[1].endsWith("\""))
				{
					first_operand_data[1] = first_operand_data[1].substring(1, first_operand_data[1].length()-1);
				}
				
				IndexType index_type = getIndexType(first_operand_data[0]);
				IndexReader reader = IndexReader.getReader(root_dir, index_type);
 				first_operand_results = reader.getPostings(first_operand_data[1]);				
			}
		}
		else
		{
			first_operand_results = first_operand.postings;
		}
		
		if(second_operand.postings == null)
		{
			if(second_operand.node_value != null)
			{
				String [] second_operand_data = second_operand.node_value.split(":");	
				
				if(second_operand_data[1].startsWith("\"") && second_operand_data[1].endsWith("\""))
				{
					second_operand_data[1] = second_operand_data[1].substring(1, second_operand_data[1].length());
				}				
				
				IndexType index_type = getIndexType(second_operand_data[0]);
				IndexReader reader = IndexReader.getReader(root_dir, index_type);
				second_operand_results = reader.getPostings(second_operand_data[1]);				
			}			
		}		
		else
		{
			second_operand_results = second_operand.postings;
		}
		
		if(operator.node_value.equalsIgnoreCase("AND"))
		{
			result_node.postings = intersect(first_operand_results, second_operand_results);
		}
		else if(operator.node_value.equalsIgnoreCase("OR"))
		{
			result_node.postings = union(first_operand_results, second_operand_results);
		}
		else if(operator.node_value.equalsIgnoreCase("NOT"))
		{
			result_node.postings = NOT(first_operand_results, second_operand_results);
		}
		
		return result_node;
	}

	private Map<String, Integer> NOT(Map<String, Integer> first_map, Map<String, Integer> second_map) 
	{
		if(second_map != null)
		{
			Set<String> second_map_docs_set = second_map.keySet();
			
			if(second_map_docs_set != null)
			{
				Iterator<String> second_map_docs_itr = second_map_docs_set.iterator();
				
				while(second_map_docs_itr.hasNext())
				{
					String doc_id = second_map_docs_itr.next();
					
					if(first_map.containsKey(doc_id))
					{
						first_map.remove(doc_id);
					}
				}
				
			}
		}
		
		return first_map;
	}

	private IndexType getIndexType(String value)
	{
		// TODO Auto-generated method stub
		IndexType index_type = IndexType.TERM;
		
		if(value.equalsIgnoreCase("Category"))
		{
			index_type = IndexType.CATEGORY;
		}
		else if(value.equalsIgnoreCase("Author"))
		{
			index_type = IndexType.AUTHOR;
		}
		else if(value.equalsIgnoreCase("Place"))
		{
			index_type = IndexType.PLACE;
		}			
		else
		{
			index_type = IndexType.TERM;
		}
		
		return index_type;
	}

	public Map<String, Integer> intersect(Map<String, Integer> first_map, Map<String, Integer> second_map)
	{
		if(first_map == null || second_map == null)
		{
			return null;
		}
		
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
	
	public Map<String, Integer> union(Map<String, Integer> first_map, Map<String, Integer> second_map)
	{
		Map<String, Integer> result_map = new HashMap<String, Integer>();		

		if(first_map != null)
		{
			if(second_map != null)
			{
				first_map.putAll(second_map);
				result_map = first_map;
			}
			else
			{
				result_map = first_map;
			}
		}
		else
		{
			result_map = second_map;
		}
		
		return result_map;		
	}
	
	public String toString()
	{
		//TODO: YOU MUST IMPLEMENT THIS
		query = "";
		
		for(int i = 0; i < query_tokens.size(); i++)
		{
			query += query_tokens.get(i).node_value + " ";
		}
		
		return "{ " + query.trim() + " }";
	}
}