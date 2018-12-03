package Master;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;

import SearchEngine.DocFreq;
import SearchEngine.Indexer;
import SearchEngine.TermDocPair;
import SearchEngine.TokenScanner;

public class QueryManager {
	private static Hashtable<Integer, String> ID2Doc = new Hashtable<Integer,String>();
	private static Hashtable<String,Integer> Doc2ID = new Hashtable<String,Integer>();
	private static Indexer indexer = new Indexer();
	
	private static Integer maxID() {
		synchronized (ID2Doc) {
			Integer max = 0; 
			for(Entry<Integer, String> entry : ID2Doc.entrySet()) {
				if(entry.getKey() > max) {
					max = entry.getKey();
				}
			}
			return max;
		}
		
	}
	
	public static String indexDoc(String docName) {
		synchronized (indexer) {
			File docFile = new File(docName);
			Integer id = 0;
			String str = "";
			if (!ID2Doc.isEmpty()) {
				id = maxID() + 1;
			}
			if(!Doc2ID.containsKey(docFile.getName())) {
				ID2Doc.put(id, docFile.getName());
				Doc2ID.put(docFile.getName(), id);
			}
			else
				return "Document already indexed!";
			
			TermDocPair[] pairs = null;
			try {
				pairs = indexer.readDoc(id, docFile.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pairs = indexer.mergeSortedList(pairs);
			indexer.addToIndex(pairs);
			System.out.println(id);
			str = id.toString() + "\n";
			return str;
		}
	}
	
	public static String indexDir(String dirPath) {
		synchronized(indexer) {
			
			File dir = new File(dirPath);
			String str = "";
			
			if (!dir.exists() || !dir.isDirectory()){
				System.err.println("documents path is wrong!");
				return "";
			}
			
			File[] docsFile = dir.listFiles();
			System.out.println("Start indexing... "  + docsFile.length );
			Integer id = 0;
			for (int i = 0; i < docsFile.length; i++) {
				if (!ID2Doc.isEmpty()) {
					id = maxID() + 1;
				}
				if(!Doc2ID.containsKey(docsFile[i].getName())) {
					ID2Doc.put(id, docsFile[i].getName());
					Doc2ID.put(docsFile[i].getName(), id);
				}
				else
					return "Directory already indexed!";
			}
			
			
			for(Integer key: ID2Doc.keySet()) {
				
				TermDocPair[] pairs = null;
				try {
					pairs = indexer.readDoc(key, dirPath +"/"+ ID2Doc.get(key));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pairs = indexer.mergeSortedList(pairs);
				indexer.addToIndex(pairs);
				System.out.println(key);
				str = key.toString();
				str = str + "\n";
			}
			
			return str;
		}
		
	}
	
	public static String search(String query) {
		synchronized (indexer) {
			String str = "";
			String[] queryTerms = new TokenScanner(query).getAllTokens();
			LinkedList<DocFreq> results = indexer.search(queryTerms);
			for (DocFreq docFreq : results) {
				System.out.println(docFreq.docID + "    " + docFreq.freq);
				str = str + docFreq.docID + "    " + docFreq.freq + "\n";
			}
			
			return str;
		}
	}
	

}
