package SearchEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.LinkedList;

public class Indexer {
	
	
	/***
	 * 
	 * @param docID: document id
	 * @param path: path of the file of document
	 * @param offset: starting position 
	 * @param len: length of the block that needs to be readed
	 * @return a sorted list of TermDocPair 
	 * @throws IOException
	 */
	public TermDocPair[] readDoc(int docID, String path, int offset, int len) throws IOException {
		
		//open the file and seek to the position
		RandomAccessFile fileStore = new RandomAccessFile("test.txt", "r");
		fileStore.seek(offset);
		
		//read the file 
		byte[] buffer = new byte[len]; 
		int i = fileStore.read(buffer);
		
		//if you read nothing
		if (i<0)
			return new TermDocPair[0];
		//else
		String line = new String(buffer, "UTF8");
		
		//remove the first word if it is not the beginning of the doc
		if (offset>0)
			line = line.replaceFirst("[^[\\w]]* ", "");
		
		//add the next word (maybe it is read partially)
		buffer = new byte[40];
		i = fileStore.read(buffer);
		String nextFewWords = new String(buffer, "UTF8");
		if (i>0) {
			line = line+nextFewWords.replaceFirst(" .*", "");
		}
		
		return tokenize(docID, new TokenScanner(line));
	}
	
	
	/***
	 * Uses tocken scanner and returns a sorted list of tockens in that document
	 * @param docID
	 * @param tscanner
	 * @return
	 */
	private TermDocPair[] tokenize(int docID, TokenScanner tscanner) {
		LinkedList<TermDocPair> pairsList = new LinkedList<>();
		String token = tscanner.getNextToken();
		while (!token.equals("")) {
			pairsList.add(new TermDocPair(token, docID, 1));
			token = tscanner.getNextToken();
		}
		TermDocPair[] arr = pairsList.toArray(new TermDocPair[0]);
		Arrays.sort(arr);
		return arr;
	}
	
	
	/**
	 * tokenize a document completely
	 * @param docID
	 * @param path
	 * @return a sorted list of term doc pairs
	 * @throws IOException
	 */
	public TermDocPair[] readDoc(int docID, String path) throws IOException {
		return tokenize(docID, new TokenScanner(new File(path)));
	}
	
	/**
	 * merges the frequency of the termdoc pair with the same term.
	 * @param arr: a sorted array of term doc pair
	 * @return merged array
	 */
	public TermDocPair[] mergeSortedList(TermDocPair[] arr) {
		LinkedList<TermDocPair> pairsList = new LinkedList<>();
		if (arr.length==0)
			return new TermDocPair[0];
		TermDocPair p = arr[0];
		
		for (int i = 1; i < arr.length; i++) {
			if (!p.merge(arr[i]))
				pairsList.add(p);
			p = arr[i];
		}
		return pairsList.toArray(new TermDocPair[0]);
	}
	
}
