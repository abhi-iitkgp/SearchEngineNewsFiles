/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.buffalo.cse.irf14.query.Node.NodeType;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser 
{
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
		
	public static Query parse(String userQuery, String defaultOperator) 
	{
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		
		if(userQuery.startsWith("{"))
		{
			userQuery = userQuery.substring(1);
		}
		if(userQuery.endsWith("}"))
		{
			userQuery = userQuery.substring(0, userQuery.length() - 1);
		}
		
		String [] queryTokens = userQuery.split("\\s+");
		ArrayList<String> tokensList = new ArrayList<String>();
		String formattedQuery = "";
		LinkedList<Node> nodes_list = new LinkedList<Node>();
		
		for(int i = 0; i < queryTokens.length; i++)
		{
			tokensList.add(queryTokens[i]);
		}
		
		for(int i = 0; i < tokensList.size(); i++)
		{
			String token_value = tokensList.get(i);
			
			if(isOperator(token_value))
			{
				// no need to do any processing
			}
			else if(isRightParenthesis(token_value))
			{
//				tokensList.set(i, "]");
			}
			else if(isLeftParenthesis(token_value))
			{
//				tokensList.set(i, "[");
			}
			else
			{		
				if(token_value.startsWith("("))
				{
					token_value = token_value.substring(1);
					tokensList.set(i, token_value);
					tokensList.add(i, "[");
				}				
				else if(token_value.contains("("))
				{
					String index_type = token_value.split(":")[0];
					String term_value = token_value.split(":")[1];
					
					term_value = term_value.substring(1);					
					tokensList.set(i, index_type + ":" + term_value);					
					term_value = tokensList.get(i+2);
					tokensList.set(i+2, index_type + ":" + term_value);					
					tokensList.add(i, "[");					
				}
				else if(!isClause(token_value))
				{
					token_value = "Term:" + token_value;
					tokensList.set(i, token_value);					
				}
				
				if(token_value.endsWith(")"))
				{
					int length = token_value.length();
					token_value = token_value.substring(0, length-1);
					tokensList.set(i, token_value);
					tokensList.add(i+1, "]");
				}				
			}
		}
		
		formattedQuery.trim();
		
		for(int i = 0; i < tokensList.size(); i++)
		{
			Node new_node = new Node();			
			new_node.node_type = getNodeType(tokensList.get(i));			
			new_node.node_value = tokensList.get(i);			
			nodes_list.add(new_node);
		}
		
		boolean start_paran_added = false;
		
		for(int i = 0; i < nodes_list.size() - 1; i++)
		{
			if(nodes_list.get(i).node_type != NodeType.clause)
			{
				continue;
			}
			
			Node current_node = nodes_list.get(i);

			String first_term =  current_node.node_value.split(":")[1];
			String first_term_index_type = current_node.node_value.split(":")[0];
			
			// both nodes should be combined if they are enclosed by double quotes
			while(first_term.contains("\"") && !first_term.endsWith("\""))
			{
				Node next_node = nodes_list.get(i+1);
				String second_term = next_node.node_value.split(":")[1];
				first_term = first_term_index_type + ":" + first_term + " " + second_term;
				current_node.node_value = first_term;
				nodes_list.remove(i+1);	
				i++;
			}			
			
			if( i < (nodes_list.size()-1))
			{
				Node next_node = nodes_list.get(i+1);
				
				if(isClause(current_node.node_value) && isClause(next_node.node_value))
				{								 
					//if two consecutive nodes are clauses then seperate them by OR operator
					if(!start_paran_added)
					{
						start_paran_added = true;
						Node left_paran = new Node(NodeType.left_parenthesis, "[", null);
						nodes_list.add(i, left_paran);
						i++;						
					}
					
					Node new_node = new Node();
					new_node.node_type = NodeType.operator;
					new_node.node_value = defaultOperator;				
					nodes_list.add(i+1, new_node);
				}
				else if(start_paran_added)
				{
					start_paran_added = false;
					Node right_paran = new Node(NodeType.right_parenthesis, "]", null);
					nodes_list.add(i+1, right_paran);
				}
			}
		}		
		
		if(start_paran_added)
		{
			start_paran_added = false;
			Node right_paran = new Node(NodeType.right_parenthesis, "]", null);
			nodes_list.addLast(right_paran);			
		}
		
		for(int i = 0; i < nodes_list.size() - 1; i++)
		{
			Node current_node = nodes_list.get(i);
			
			if(current_node.node_type == NodeType.operator)
			{
				if(current_node.node_value.equalsIgnoreCase("NOT"))
				{
					current_node.node_value = "AND";
					Node next_node = nodes_list.get(i+1);
					
					next_node.node_value = "<" + next_node.node_value + ">";
				}
			}
		}
		
		// Adding left parenthesis at the begining
		Node start_parenthesis = new Node();
		start_parenthesis.node_type = NodeType.left_parenthesis;
		start_parenthesis.node_value = "{";
		
		nodes_list.addFirst(start_parenthesis);
		
		// Adding right parenthesis at the end
		Node end_parenthesis = new Node();
		end_parenthesis.node_type = NodeType.right_parenthesis;
		end_parenthesis.node_value = "}";
		
		nodes_list.addLast(end_parenthesis);				
		
		for(int i = 0;i < nodes_list.size(); i++)
		{
			formattedQuery = formattedQuery + nodes_list.get(i).node_value + " "; 
		}		
		
		formattedQuery.trim();
//		System.out.println(formattedQuery);				
		
 		return new Query(nodes_list);
	}

	private static NodeType getNodeType(String string) 
	{
		// TODO Auto-generated method stub
		NodeType node_type = null;
		
		if(isClause(string))
		{
			node_type = NodeType.clause;
		}
		else if(isOperator(string))
		{
			node_type = NodeType.operator;
		}
		else if(isLeftParenthesis(string))
		{
			node_type = NodeType.left_parenthesis;
		}
		else if(isRightParenthesis(string))
		{
			node_type = NodeType.right_parenthesis;
		}
		
		return node_type;
	}

	private static boolean isRightParenthesis(String string)
	{
		// TODO Auto-generated method stub
		boolean return_value = false;
		
		  if(string.equalsIgnoreCase("}") || string.equalsIgnoreCase("]") || string.equalsIgnoreCase(")"))
		  {
			  return_value = true;
		  }
		
		return return_value;
	}

	private static boolean isLeftParenthesis(String string)
	{
		// TODO Auto-generated method stub
		boolean return_value = false;
		
		  if(string.equalsIgnoreCase("{") || string.equalsIgnoreCase("[") || string.equalsIgnoreCase("("))
		  {
			  return_value = true;
		  }
		
		return return_value;
	}

	private static boolean isClause(String input) 
	{
		// Types of index
		String [] index_types = {"Term", "Category", "Author", "Place"};
		boolean isClause = false;
		
		
		// A clause has a index type followed by colon followed by value
		if(input.contains(":"))
		{
			String [] input_parts = input.split(":");
			
			for(int i = 0; i < index_types.length; i++)
			{
				if(input_parts[0].equalsIgnoreCase(index_types[i]))
				{
					isClause = true;
				}
			}			
		}		
		
		return isClause;
	}

	private static boolean isOperator(String input) 
	{
		String [] operator_values = {"OR", "AND", "NOT"};
		boolean isIndex = false;
		
		for(int i = 0; i < operator_values.length; i++)
		{
			if(input.equalsIgnoreCase(operator_values[i]))
			{
				isIndex = true;
			}
		}
		
		return isIndex;
	}
}