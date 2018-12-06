package Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import SearchEngine.Indexer;

public class Helper {
	static Socket masterSocket;
	static ServerSocket socket;
	static int ID;
	static String ip;
	static int portM;
	static int portH;
	static String[] ipList;
	static int[] portList;
	static String[] othersAddr;
	private static BufferedReader inputSteam;
	private static OutputStreamWriter outputStream;
	private static BufferedReader[] inputSteamList;
	private static OutputStreamWriter[] outputStreamList;
	
	private static Indexer indexer = new Indexer();
	private static boolean waitingForMaster = true;
	private static boolean waitingForOtherHelper = true;
	
	
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
					for (int i = 0; i < strArr.length; i++) {
						
					}

				}

			}
			
			
			
			
			//send to others
			
			
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
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
						String connector = in.readLine();
						System.out.println("Helper" +connector +" connected to Helper" + ID);
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
		inputSteamList= new BufferedReader[portList.length]; 
		outputStreamList = new OutputStreamWriter[portList.length];
		
		for (int i = 0; i < portList.length; i++) {
			if(i > ID) {
				try {
					Socket s = new Socket(ipList[i], portList[i]);
					inputSteamList[i] = new BufferedReader( 
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
