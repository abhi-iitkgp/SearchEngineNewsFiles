/**
 * 
 */
package edu.buffalo.cse.irf14;

import java.io.File;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		String ipDir = "D:\\IR_PROJECT_1\\newsindexer-master\\training";
		String indexDir = "D:\\IR_PROJECT_OUTPUT";
		//more? idk!
		
		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();
		
		String[] files;
		File dir;
		
		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);
		String file_path = "";
		try {
			for (String cat : catDirectories) {
				dir = new File(ipDir+ File.separator+ cat);
				files = dir.list();
				
				if (files == null)
					continue;
				
				for (String f : files) {
					try {
						
//						if(f.equalsIgnoreCase("0009566"))
//						{
//							System.out.println("this file is throwing exception");
//						}
						
						file_path = dir.getAbsolutePath() + File.separator +f;
						d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						writer.addDocument(d);
					} catch (ParserException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					} 
					
				}
				
			}
			
			writer.close();

			
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			System.out.println(file_path);
			e.printStackTrace();
		}
	}

}
