package Master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;

import Helper.Helper;
import SearchEngine.DocFreq;
import SearchEngine.Indexer;
import SearchEngine.TermDocPair;
import SearchEngine.TokenScanner;

public class Master {
	private static Hashtable<Integer, String> ID2Doc = new Hashtable<Integer,String>();
	private static Hashtable<String,Integer> Doc2ID = new Hashtable<String,Integer>();
	private static Hashtable<Integer,Integer> ID2Port = new Hashtable<Integer,Integer>();
	private static int i = -1;
	private static BufferedReader[] inputSteamList;
	private static OutputStreamWriter[] outputStreamList;
	private static Socket[] sockets;
	private static int numOfHelpers;
	private static Hashtable<Integer, String[]> Helper2Doc = new Hashtable<Integer, String[]>();
	private static Hashtable<Integer, Integer> Doc2Helper = new Hashtable<Integer, Integer>();
	private static Hashtable<Integer,ArrayList<Integer>> AssignedList;
	
	
	private static Indexer indexer = new Indexer();
	private static int nextHelperID() {
		return i++;
	}
	
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
				pairs = indexer.readDoc(id,  "docs/" + docFile.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pairs = indexer.mergeSortedList(pairs);
			indexer.addToIndex(pairs);
			System.out.println(id);
			str = id.toString()+ ": "+ ID2Doc.get(id)+ "\n";
			return str;
		}
	}
	
	public static String indexDir(String dirPath) {
		synchronized(indexer) {
			
			File dir = new File(dirPath);
			String str = "";
			String pathSlash = dirPath + "/";
			
			if (!dir.exists() || !dir.isDirectory()){
				System.err.println("documents path is wrong!");
				return "";
			}
			
			File[] docsFile = dir.listFiles();
			Integer id = 0;
			for (int i = 0; i < docsFile.length; i++) {
				if (!ID2Doc.isEmpty()) {
					id = maxID() + 1;
				}
				if(!Doc2ID.containsKey(docsFile[i].getName())) {
					ID2Doc.put(id, docsFile[i].getName());
					Doc2ID.put(docsFile[i].getName(), id);
				}

			}
			
//			
//			for(Integer key: ID2Doc.keySet()) {
//				
//				TermDocPair[] pairs = null;
//				try {
//					pairs = indexer.readDoc(key, dirPath +"/"+ ID2Doc.get(key));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				pairs = indexer.mergeSortedList(pairs);
//				indexer.addToIndex(pairs);
//				System.out.println(key);
//				str = str + key.toString()+ ": "+ ID2Doc.get(key)+ "\n";
//			}
			
			System.out.println("Master issues index to helpers");
			issueIndex(pathSlash);
			
			return dirPath;
		}
		
	}
	
	public static String search(String query) {
		synchronized (indexer) {
			String str = "";
			String[] queryTerms = new TokenScanner(query).getAllTokens();
			LinkedList<DocFreq> results = indexer.search(queryTerms);
			for (DocFreq docFreq : results) {
				System.out.println(docFreq.docID + "    " + docFreq.freq);
				
				str = str + ID2Doc.get(docFreq.docID ) + "    " + docFreq.freq + "\n";
			}
			
			return str;
		}
	}
	
	public static void connectToHelpers(String[] list) {
		inputSteamList= new BufferedReader[list.length]; 
		outputStreamList = new OutputStreamWriter[list.length];
		numOfHelpers = list.length;
		
		String[] ipPortPort;
		for (int i = 0; i < list.length; i++) {
			ipPortPort = list[i].split(":");
			String ip = ipPortPort[0];
			int portM = Integer.parseInt(ipPortPort[1]);
			int portH = Integer.parseInt(ipPortPort[2]);
			try {
				Socket socket = new Socket(ip, portM);
				inputSteamList[i] = new BufferedReader( 
						new InputStreamReader(socket.getInputStream())); 
				outputStreamList[i] = new OutputStreamWriter(socket.getOutputStream());
				String str = "";
				for (int j = 0; j < list.length; j++) {
					str = str + list[j] + " ";
				}
				str = str + "\n";
				outputStreamList[i].write(str);
				outputStreamList[i].flush();
				
			} catch (NumberFormatException e) {
				System.out.println(e);
			} catch (UnknownHostException e) {
				System.out.println("failed connect to "+ list[i]);
			} catch (IOException e) {
				System.out.println("failed connect to "+ list[i]);
			}
		}		
		
	}
	
	public static boolean getHelpersAck() {
		for (int i = 0; i < inputSteamList.length; i++) {
			String ack;
			try {
				ack = inputSteamList[i].readLine();
				if(!ack.equals("ok")) {
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return true;
	}
	
	
	public static void broadcast(String cmd) {
		for (int i = 0; i < outputStreamList.length; i++) {
			try {
				outputStreamList[i].write(cmd+"\n");
				outputStreamList[i].flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void sendToHelper(int id, String msg) {
		try {
			outputStreamList[id].write(msg+"\n");
			outputStreamList[id].flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void issueIndex(String path) {
		//assign docs to helpers in RR fashion
		AssignedList =  new Hashtable<Integer,ArrayList<Integer>>();
		
		for (int i = 0; i < numOfHelpers; i++) {
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for(Integer key: ID2Doc.keySet()) {
				if(key % numOfHelpers == i) {
					arr.add(key);
				}	
			}
			AssignedList.put(i,arr);
		}
		
		broadcast("index");
		
		for (Integer i: AssignedList.keySet()) {
			String str = "";
			for (int k = 0; k < AssignedList.get(i).size(); k++) {
				str = str + AssignedList.get(i).get(k).toString() + ":" 
						+ path +ID2Doc.get(AssignedList.get(i).get(k)) + " ";			
			}
			sendToHelper(i, str);
			
		}
		
		if (!Master.getHelpersAck()) {
			System.out.println("Helpers index failed!");
			return;
		}	
		System.out.println("Master got index Ack from helpers");
		
		broadcast("reduce");
		
		if (!Master.getHelpersAck()) {
			System.out.println("Helpers reduce failed!");
			return;
		}	
		System.out.println("Master got reduce Ack from helpers");
		
	}
	
}

