import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import SearchEngine.DocFreq;
import SearchEngine.Indexer;
import SearchEngine.TermDocPair;
import SearchEngine.TokenScanner;

public class Main {
	public static void main(String[] args) throws IOException {
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
		System.out.println("searching");
		String query = "rectangular panels and for rectangular";
		String[] queryTerms = new TokenScanner(query).getAllTokens();
		LinkedList<DocFreq> results = indexer.search(queryTerms);
		for (DocFreq docFreq : results) {
			System.out.println(docFreq.docID + "    " + docFreq.freq);
		}
	}
}
