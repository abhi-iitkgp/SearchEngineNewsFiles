package edu.buffalo.cse.irf14.query;

import java.util.Map;

class Node
{
	public enum NodeType
	{
		clause,
		operator,
		left_parenthesis,
		right_parenthesis,
		none
	}
	
	NodeType node_type;
	String node_value;
	Map<String, Integer> postings;
	
	public Node(NodeType nodeType, String nodeValue, Map<String, Integer> all_postings)
	{
		node_type = nodeType;
		node_value = nodeValue;
		postings = all_postings;
	}
	
	public Node()
	{
		
	}
}