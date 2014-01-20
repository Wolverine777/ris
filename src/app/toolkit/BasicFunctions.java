package app.toolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * @author Benjamin Reemts
 *
 */

public class BasicFunctions {

	public static String[] readFile(File f){
		LinkedList<String> lines=new LinkedList<String>();
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(f));
	        String line = br.readLine();
	        
	        while (line != null) {
	        	lines.add(line);
	        	lines.add("\n");
	            line = br.readLine();
	        }
	        br.close();
	    }catch(Exception e){
	    	System.out.println(e.getMessage());
	    }
		return lines.toArray(new String[0]);
	}
}
