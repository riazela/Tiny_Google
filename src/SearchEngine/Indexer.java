package SearchEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class Indexer {
	
	InvertedList invertedList;
	
	public Indexer() {
		invertedList = new InvertedList();
	}
	
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
		TermDocPair[] arr = pairsList.toArray(new TermDocPair[pairsList.size()]);
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
	
	public TermDocPair[] readDocs(int[] docID, String[] path) throws IOException {
		LinkedList<TermDocPair> pairsList = new LinkedList<>();
		for (int i = 0; i < path.length; i++) {
			TokenScanner tscanner = new TokenScanner(new File(path[i]));
			String token = tscanner.getNextToken();
			while (!token.equals("")) {
				pairsList.add(new TermDocPair(token, docID[i], 1));
				token = tscanner.getNextToken();
			}
			
		}
		TermDocPair[] arr = pairsList.toArray(new TermDocPair[pairsList.size()]);

		return arr;
	}
	
	
	/**
	 * merges the frequency of the termdoc pair with the same term.
	 * @param arr: an array of term doc pair
	 * @return a sorted and merged array
	 */
	public TermDocPair[] mergeSortedList(TermDocPair[] arr) {
		Arrays.sort(arr);
		LinkedList<TermDocPair> pairsList = new LinkedList<>();
		if (arr.length==0)
			return new TermDocPair[0];
		TermDocPair p = arr[0];
		
		for (int i = 1; i < arr.length; i++) {
			if (!p.merge(arr[i])) {
				pairsList.add(p);
				p = arr[i];
			}
		}
		pairsList.add(p);
		return pairsList.toArray(new TermDocPair[0]);
	}
	
	public void addToIndex(TermDocPair[] pairs) {
		for (TermDocPair termDocPair : pairs) {
			invertedList.addTermDocPair(termDocPair);
		}
	}
	
	public void addToIndex(TermDocPair pair) {
			invertedList.addTermDocPair(pair);
		
	}
	
	/**
	 * searches all the terms and returns all the results that contains all of the terms along with their scores as the freq part (not sorted)
	 * @param terms: array of the terms
	 * @return list of document frequency pairs in which frequency is the score.
	 */
	public LinkedList<DocFreq> search(String[] terms) {
		//if there was no term
		if (terms.length==0)
			return new LinkedList<>();
		//take the first term and make the initial result list
		LinkedList<DocFreq> results = new LinkedList<>();
		for (int i = 0; i < terms.length; i++) {
			LinkedList<DocFreq> termResult = invertedList.getListOf(terms[i]);
			for (DocFreq docFreq : termResult) {
				results.add(new DocFreq(docFreq.docID, docFreq.freq));
			}
		}
		
		return results;
	}
	
	public LinkedList<DocFreq> resultOfTheSearch(String[] terms, DocFreq[] partialResults){
		Arrays.sort(partialResults);
		int counter = 1;
		LinkedList<DocFreq> results = new LinkedList<>();
		for (int i = 1; i < partialResults.length; i++) {
			if (partialResults[i].docID==partialResults[i-1].docID) {
				counter++;
				partialResults[i].freq += partialResults[i-1].freq;
			}
			else
				counter=1;
			if (counter==terms.length)
				results.add(partialResults[i]);
		}
		return results;
	}
	
	public LinkedList<DocFreq> MergeLists(LinkedList<DocFreq> result, LinkedList<DocFreq> termResult){
		return null;
	}
	
	public void saveToFile(String path) throws IOException {
		File file = new File(path);
		FileWriter writer = new FileWriter(file);
		Set<String> terms = invertedList.lookupTable.keySet();
		for (String term : terms) {
			writer.write(term);
			for (DocFreq df: invertedList.getListOf(term)) {
				writer.write(" " + df.docID+" "+df.freq);
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}
	
	public static Indexer loadFromFile(String path) throws IOException {
		File file = new File(path);
		Scanner scanner = new Scanner(file);
		Indexer indexer = new Indexer();
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Scanner linescanner = new Scanner(line);
			String term = linescanner.next();
			while (linescanner.hasNext())
				indexer.addToIndex(new TermDocPair(term, linescanner.nextInt(), linescanner.nextInt()));
		}
		return indexer;
	}
	
}
