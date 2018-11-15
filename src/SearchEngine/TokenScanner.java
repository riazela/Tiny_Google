package SearchEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class TokenScanner {
	private String filePath = null;
	private Scanner scanner;
	
	LinkedList<String> tokQueue;
	
	public TokenScanner(String path) {
		openFile(path);
		tokQueue = new LinkedList<>();
	}
	
	public boolean openFile(String path){
		if (filePath != null)
			return false;
		
		try {
			scanner = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			System.err.println(path+" not found");
			return false;
		}
		filePath = path;
		
		return true;
	}
	
	
	
	public void closeFile(){
		if (filePath != null){
			filePath= null;
			scanner.close();
		}
	}
	
	
	/***
	 * 
	 * @return the next token if there exists one or "" if there exists no token anymore
	 */
	public String getNextToken(){
		String s = "";
		while (scanner.hasNext() && (!s.matches(".*([a-z]|[1-9])+.*"))){
			if (tokQueue.isEmpty())
				s = scanner.next();
			else
				s = tokQueue.poll();
			
			//copy of normalize if you changed here you should change normalize too
			if (!s.matches(".*([a-z]|[1-9])+.*"))
				return "";
			//do not split email address or website address
			if (!(s.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" 
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$") || s.matches("(https|https|www|ftp)+.*"))){
				String[] temp = s.split("(\\(|\\)|\\\"|\\\'|/|\\\\|:|,|\\.|;|\\-|\\_|\\[|\\])+");
				for (int i = 1; i < temp.length; i++) {
					tokQueue.add(temp[i]);
				}
				s = temp[0];
				
				s = s.toLowerCase();
			}
		}
		return s;
	}
	
	public String[] getAllTokens(){
		scanner.reset();
		ArrayList<String> strings = new ArrayList<String>();
		String s=getNextToken();
		while (s!= null){
			if (!s.equals(""))
				strings.add(s);
			s = getNextToken();
		}
		if (strings.size() == 0)
			return new String[0];
		return strings.toArray(new String[1]);
	}
	
}
