package Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

import SearchEngine.DocFreq;
import SearchEngine.Indexer;
import SearchEngine.TermDocPair;
import SearchEngine.TokenScanner;

public class Helper {
	static Socket masterSocket;
	static ServerSocket socket;
	static int ID;
	static String ip;
	static int portM;
	static int portH;
	static String[] ipList;
	static int[] portList;
	private static int numOfHelpers;
	static String[] othersAddr;
	private static BufferedReader inputSteam;
	private static OutputStreamWriter outputStream;
	private static BufferedReader[] inputStreamList;
	private static OutputStreamWriter[] outputStreamList;
	
	private static Indexer indexer = new Indexer();
	private static boolean waitingForMaster = true;
	private static boolean waitingForOtherHelper = true;
	private static TermDocPair[] ownPairs;
	private static LinkedList<TermDocPair> allPairs = new LinkedList<>();
	
	
	public static void main(String[] args) {
		if (args.length != 1) {
		      System.err.println("Usage: Helper <Master Listening Port>");
		      System.exit(-1);
		    }
		String[] ipPort = args[0].split(":");
		ip = ipPort[0];
		portM = Integer.parseInt(ipPort[1]);
		listenForMaster();
		
	}
	
	/**
	 * This method waits for a master to connect and then creates an instance of Helper using its socket
	 * @param port
	 */
	public static void listenForMaster() {
		
		try {
			socket = new ServerSocket(portM);
			System.out.println("created");
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			masterSocket = socket.accept();
			System.out.println("accepted");
			inputSteam = new BufferedReader( 
					new InputStreamReader(masterSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
			outputStream = new OutputStreamWriter(masterSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String cmd;
		try {
			//
			cmd = inputSteam.readLine();
			System.out.println(cmd);
			String[] list = cmd.split(" ");
			
			setup(list);
			
			//listen for others
			listenForOthers();
			
			// ack to master
			sendMasterAck();
			
			//get connect cmd from master to connect
			cmd = inputSteam.readLine();
			
			//connect to others with greater ID than us
			if(cmd.equals("connect")) {
				connectToOthers();
			}
			
			sendMasterAck();
			
			// ############## As of here make it in a loop!!!!!!
			//get index cmd from master
			while(true) {
				cmd = inputSteam.readLine();
				if(cmd.equals("index")) {
					String str = inputSteam.readLine();
					String strArr[] = str.split(" ");
					int [] docID = new int[strArr.length];
					String[] path = new String[strArr.length];
					for (int i = 0; i < strArr.length; i++) {
						String[] idDoc = strArr[i].split(":");
						docID[i] = Integer.parseInt(idDoc[0]);
						path[i] =  idDoc[1];
					}

					indexOwnPart(docID, path);
					sendToOthers();
					sendMasterAck();
				}
				else if(cmd.equals("reduce")) {
					addOwnPairsToAll();
					getFromOthers();
					mergeAllTogether();
					indexer.saveToFile(String.valueOf(ID));
					sendMasterAck();
				} else if(cmd.equals("search")) {
					String str = inputSteam.readLine();
					search(str);
				}
				else if(cmd.equals("reset")) {
					indexer = new Indexer();
				}
				else if(cmd.equals("save")) {
					String str = inputSteam.readLine();
					indexer.saveToFile(str);
				}
				else if(cmd.equals("laod")) {
					String str = inputSteam.readLine();
					indexer = Indexer.loadFromFile(str);
				}

			}
			
			
			
			
			//send to others
			
			
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private static void search(String str) throws IOException {
		LinkedList<DocFreq> partialResult = indexer.search((new TokenScanner(str)).getAllTokens());
		for (DocFreq docFreq : partialResult) {
			outputStream.write(docFreq.docID + ":" + docFreq.freq + " ");
		}
		outputStream.write("\n");
	}
	
	
	private static void mergeAllTogether() {
		// TODO Auto-generated method stub
		TermDocPair[] allPairsArr = allPairs.toArray(new TermDocPair[allPairs.size()]);
		allPairsArr = indexer.mergeSortedList(allPairsArr);
		indexer.addToIndex(allPairsArr);
	}

	public static void indexOwnPart(int[] docID, String[] path) {
		
		try {
			ownPairs = indexer.readDocs(docID, path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ownPairs = indexer.mergeSortedList(ownPairs);	
		
		
//		for (int i = 0; i < pairs.length; i++) {
//			System.out.println(pairs[i].term + " in doc" + pairs[i].doc +": "+ pairs[i].freq);
//		}
		
	}
	public static void sendMasterAck() {
		try {
			outputStream.write("ok\n");
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setup(String[] list) {
		//TODO: get our ID
		//TODO: get our port
		//TODO: get the list of other IP Addresses and their id and ports
		//TODO: listen to your own port 
		ipList = new String[list.length];
		portList = new int[list.length];
		numOfHelpers = list.length;
		inputStreamList= new BufferedReader[portList.length]; 
		outputStreamList = new OutputStreamWriter[portList.length];
		for (int i = 0; i < list.length; i++) {
			String[] str = list[i].split(":");
			ipList[i] = str[0];
			portList[i] = Integer.parseInt(str[2]);
			if(Integer.parseInt(str[1]) == portM) {
				ID = i;
				portH = portList[i];
			}
		}
		System.out.println("Helper" + ID +" listeing @ " + portH);
		
		
	}
	
	
	public static void sendToOthers() {
		String str = "";
		
		for (int i = 0; i < outputStreamList.length; i++) {
			for (int k = 0; k < ownPairs.length; k++) {
				if(ownPairs[k].term.hashCode() % numOfHelpers == i) {
					str = str + ownPairs[k].term
							+ ":" + String.valueOf(ownPairs[k].doc)
							+ ":" + String.valueOf(ownPairs[k].freq) + " ";			
				}
			}
			
			try {
				if(i != ID) {
					outputStreamList[i].write(str+"\n");
//					outputStreamList[i].write("dream:1:1 \n");
					outputStreamList[i].flush();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void getFromOthers() {
		
		for (int i = 0; i < inputStreamList.length; i++) {
			if(i != ID) {
				try {
					String str = inputStreamList[i].readLine();
					String[] strArr = str.split(" ");
					for (int t = 0; t < strArr.length; t++) {
						String[] termdocfreq = strArr[t].split(":");
						String term = termdocfreq[0];
						int doc = Integer.parseInt(termdocfreq[1]);
						int freq = Integer.parseInt(termdocfreq[2]);
						TermDocPair newPair = new TermDocPair(term, doc, freq);
						allPairs.add(newPair);
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	public static void addOwnPairsToAll() {
		for (int i = 0; i < ownPairs.length; i++) {
			if(ownPairs[i].term.hashCode() % numOfHelpers == ID)
				allPairs.add(ownPairs[i]);
		}
		ownPairs = null;
	}
	

	
	public static void listenForOthers() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				ServerSocket serverSocket;
				try {
					 serverSocket = new ServerSocket(portH);
					 serverSocket.setSoTimeout(1000);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
					
				while (waitingForOtherHelper) {
					Socket s;
					try {
						s = serverSocket.accept();
						BufferedReader in = new BufferedReader( 
								new InputStreamReader(s.getInputStream())); 
						OutputStreamWriter out = new OutputStreamWriter(s.getOutputStream());
						String connector = in.readLine();
						System.out.println("Helper" +connector +" connected to Helper" + ID);
						inputStreamList[Integer.parseInt(connector)] = in;
						outputStreamList[Integer.parseInt(connector)] = out;
					}
					catch (SocketTimeoutException e){
						continue;
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		
	}
	
	public static void connectToOthers() {
		//TODO: connect to those helpers that have ID greater than us
		
		for (int i = 0; i < portList.length; i++) {
			if(i > ID) {
				try {
					Socket s = new Socket(ipList[i], portList[i]);
					inputStreamList[i]= new BufferedReader( 
							new InputStreamReader(s.getInputStream()));
					outputStreamList[i] = new OutputStreamWriter(s.getOutputStream());
					outputStreamList[i].write(ID+"\n");
					outputStreamList[i].flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("cannot connect to " + ipList[i] +":"+ String.valueOf(portList[i]));
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	public static void indexDoc(){
		
	}
	
}
