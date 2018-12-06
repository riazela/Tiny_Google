import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;

import SearchEngine.DocFreq;
import SearchEngine.Indexer;
import SearchEngine.TermDocPair;
import SearchEngine.TokenScanner;

public class test {
    public static void main(String[] args) throws IOException{
    	Indexer indexer = new Indexer();
		String docsFolderPath = "Docs/";
		File dir = new File("Docs/");
		
		if (!dir.exists() || !dir.isDirectory()){
			System.err.println("documents path is wrong!");
			return;
		}
		
		File[] docsFile = dir.listFiles();
		int[] docIDs = new int[docsFile.length];
		String temp;
		for (int i = 0; i < docIDs.length; i++) {
			temp = docsFile[i].getName();
			docIDs[i] = Integer.parseInt(temp.replaceAll("doc", ""));
		}
		
		Arrays.sort(docIDs);
		
		for (int i = 0; i < docIDs.length; i++) {
//		for (int i = 0; i < 1; i++) {
			TermDocPair[] pairs = indexer.readDoc(docIDs[i], docsFolderPath + "doc" + docIDs[i]);
			pairs = indexer.mergeSortedList(pairs);
			indexer.addToIndex(pairs);
			System.out.println(i);
		}
		
		
		//how to search
		System.out.println("searching");
		String query = "rectangular panels and for rectangular";
		String[] queryTerms = new TokenScanner(query).getAllTokens();
		LinkedList<DocFreq> results = indexer.search(queryTerms);
		results = indexer.resultOfTheSearch(queryTerms, results.toArray(new DocFreq[0]));
		
		//how to sort final results descending
		DocFreq[] sortedResults = results.toArray(new DocFreq[0]);
		Arrays.sort(sortedResults, new Comparator<DocFreq>() {
			@Override
			public int compare(DocFreq o1, DocFreq o2) {
				return o2.freq-o1.freq;
			}
		});
		
		//printing the final result
		for (DocFreq docFreq : sortedResults) {
			System.out.println(docFreq.docID + "    " + docFreq.freq);
		}
		
//		System.out.println("writing index");
//		indexer.saveToFile("index.txt");
//		indexer = Indexer.loadFromFile("index.txt");
//		
//		System.out.println("searching 2");
//		 query = "rectangular panels and for rectangular";
//		queryTerms = new TokenScanner(query).getAllTokens();
//		results = indexer.search(queryTerms);
//		for (DocFreq docFreq : results) {
//			System.out.println(docFreq.docID + "    " + docFreq.freq);
//		}
    }
}
