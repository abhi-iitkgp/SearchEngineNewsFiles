/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser 
{
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException 
	{
		// TODO YOU MUST IMPLEMENT THIS
		
		if(filename == null)
		{
			throw new ParserException();
		}
		
		File f = new File(filename);

		if(!f.exists())
		{
			throw new ParserException();
		}
		
		String fileName = f.getName();
		String category = f.getAbsoluteFile().getParentFile().getName();
		Document document= new Document();		
		
		document.setField(FieldNames.FILEID, fileName);
		document.setField(FieldNames.CATEGORY, category);
//		boolean exists = f.exists();
//		boolean canRead = f.canRead();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));			
			
			String title = ParseTitle(br);
			document.setField(FieldNames.TITLE, title);	
			
			ParseAuthorField(br, document);			
			String contentFirstLine = ParsePlaceAndDate(br, document);							
			
			String content = ParseContent(br);
			
			if(contentFirstLine != null)
			{
				content = contentFirstLine + "\r\n" + content;
			}
			
			document.setField(FieldNames.CONTENT,content);			
			br.close();
		}
		catch (Exception e) 
		{	
			throw new ParserException();
		}	
		
//		PrintDocumentFields(document);		
		return document;
	}
	
//	private static void ReadEmptyLines(BufferedReader br) throws IOException
//	{
//		// TODO Auto-generated method stub
//		String line;
//		
//		// Reads all lines with no text
//		
//				
//		PushbackReader pushback_reader = new PushbackReader(br, line.length() + 1);
//		pushback_reader.unread(line.toCharArray());	
//		System.out.println("pushbacked line is: " + line);
//	}

//	private static void PrintDocumentFields(Document document) 
//	{
//		System.out.println("Category: " + document.getField(FieldNames.CATEGORY)[0]);
//		System.out.println("Title: " + document.getField(FieldNames.TITLE)[0]);
//		
//		if((document.getField(FieldNames.AUTHOR)) != null)
//		{
//			System.out.println("Author: " + document.getField(FieldNames.AUTHOR)[0]);
//		}
//
//		if((document.getField(FieldNames.AUTHORORG)) != null)
//		{
//			System.out.println("Author org: " + document.getField(FieldNames.AUTHORORG)[0]);
//		}		
//		
//		System.out.println("Place: " + document.getField(FieldNames.PLACE)[0]);
//		System.out.println("Date: " + document.getField(FieldNames.NEWSDATE)[0]);
////		System.out.println("Content: " + document.getField(FieldNames.CONTENT)[0]);
//	}

	private static void ParseAuthorField(BufferedReader br, Document d) throws IOException
	{
		String author, author_org;
		
		br.mark(1000);
		String line = br.readLine();
		
		int startIndex = -1;
		int endIndex = -1;
		
		while((line = br.readLine()).length() == 0);
		
		boolean hasStartTag = line.contains("<AUTHOR>");
		boolean hasCloseTag = line.contains("</AUTHOR>");
		
		if(hasStartTag && hasCloseTag)
		{
			startIndex = line.indexOf("<AUTHOR>") + new String("<AUTHOR>").length();
			endIndex = line.indexOf("</AUTHOR>");
				
			line = line.substring(startIndex, endIndex).trim();
			
			if(line.contains(","))
			{
				int start_index = line.toLowerCase().indexOf(("by "));	
				int end_index = line.indexOf(",");
				author = line.substring(start_index+3, end_index);				
				author_org = line.substring(end_index + 1).trim();				
				d.setField(FieldNames.AUTHOR, author);
				d.setField(FieldNames.AUTHORORG, author_org);
			}
			else
			{
				int star_index = line.toLowerCase().indexOf(("by "));				
				author = line.substring(star_index + 3);
				d.setField(FieldNames.AUTHOR, author);
			}
		}
		else
		{
			br.reset();
		}	
	}
	
	private static String ParseTitle(BufferedReader br) throws IOException
	{
		String title;
		while((title = br.readLine().trim()).length() == 0);
		
		return title;
	}
	
//	private static String ParseDate(BufferedReader br) throws IOException
//	{
//		char c;
//		StringBuilder date = new StringBuilder();
//		
//		while( (c = (char) br.read()) != '-')
//		{
//			date.append(c);
//		}
//		
//		System.out.println("Date is: " + date.toString().trim());
//		return date.toString().trim();
//	}
	
	private static String ParsePlaceAndDate(BufferedReader br, Document d) throws IOException, ParserException
	{		
		String line;
		String place;
		String contentFirstLine = null;
		String date;
		
		br.mark(1000);		
		
		line = br.readLine();
		
		while(line.trim().length() == 0)
		{
			line = br.readLine();
		}
		
		int startIndex = FindDateStartIndex(line);
		int endIndex = startIndex;
		
		if(startIndex > 0)
		{
			endIndex = line.indexOf('-', startIndex);	
			
			if(endIndex < 0)
			{
				throw new ParserException();
			}
			
			place = line.substring(0, startIndex-1).trim();
			
			if(place.endsWith(","))
			{
				place = place.substring(0, place.length()-1);
			}
					
			date = line.substring(startIndex, endIndex).trim();
			
			if((endIndex + 1) < line.length())
			{
				contentFirstLine = line.substring(endIndex+2).trim();				
			}
			
			if(place.length() > 0 && date.length() > 0)
			{
				d.setField(FieldNames.PLACE, place);				
				d.setField(FieldNames.NEWSDATE, date);				
			}
					
		}	
		
		if(d.getField(FieldNames.PLACE) == null)
		{
			br.reset();
			return null;
		}
		
		return contentFirstLine;	
	}
	
	private static int FindDateStartIndex(String line) 
	{
		String [] monthFormats = {"jan ", "feb ", "mar ", "apr ",
									"may ", "jun ", "july ", "aug ", "sept ",
									"oct ", "nov ", "dec ", "january ", "february ",
									"march ", "april ", "may ", "june ", "july ", "august ",
									"september ","october ", "november ", "december "};
		
		int index = -1;
		int j;
		
		for(int i = 0; i < monthFormats.length; i++)
		{
			if((j = line.toLowerCase().indexOf(monthFormats[i])) > 0)
			{
				if(index < 0 || j < index)
				{
					index = j;
				}
			}
		}		
		
		return index;
	}

	private static String ParseContent(BufferedReader br) throws IOException
	{
		StringBuilder content = new StringBuilder();
		String l;
		
		while((l = br.readLine()) != null)
		{
			content.append(l);
			content.append("\n");
		}
		
		return content.toString().trim();
	}
}