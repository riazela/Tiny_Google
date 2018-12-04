package Master;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class Client {
	public static ArrayList<Client> allClients = new ArrayList<Client>();
	private static boolean waitingForNewClients = true;
	
		
	public static void waitForClient(int port) {
		final int mport = port;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ServerSocket serverSocket;
				try {
					 serverSocket = new ServerSocket(mport);
					 serverSocket.setSoTimeout(1000);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
					
				while (waitingForNewClients) {
					Socket s;
					try {
						s = serverSocket.accept();
						System.out.println("one client connected");
						allClients.add(new Client(s));
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
	
	public static void stopEverything() {
		waitingForNewClients = false;
		for (Client c: allClients) {
			c.close();
		}
	}
	
	
	private Socket socket;
	private BufferedReader inputSteam;
	private OutputStreamWriter outputStream;
	
	
	public Client(Socket socket) throws IOException {
		this.socket = socket;
		
		socket.setSoTimeout(1000);
		
		this.inputSteam = new BufferedReader( 
				new InputStreamReader(socket.getInputStream())); 
		this.outputStream = new OutputStreamWriter(socket.getOutputStream());
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Client.this.listen();
				} catch (IOException e) {
					e.printStackTrace();
					Client.this.close();
				}
			}
		});
		t.start();
	}
	
	public void listen () throws IOException {
		String messagestr;
		while (true) {
			try {
				messagestr = inputSteam.readLine();
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				this.close();
				return;
			} 
			String[] messageParts = messagestr.split(" ");
			String str = "";
			switch (messageParts[0]) {
			case "indexdir":{
				str = QueryManager.indexDir(messageParts[1]);
				str = "directory indexed: \n" + str;
				str = str + "\n";
				outputStream.write(str);
				outputStream.flush();
				break;
			}
			case "indexdoc":{
				str = QueryManager.indexDoc(messageParts[1]);
				str = "document indexed: \n" + str;
				str = str + "\n";
				outputStream.write(str);
				outputStream.flush();
				break;
			}
			case "search":{
				String query = messagestr.replace("search ", "");
				str = QueryManager.search(query);
				str = str + "\n";
				outputStream.write(str);
				outputStream.flush();
				break;
			}
			case "close":{
				this.close();
				return;
			}
			default:
				break;
			}
		}
	}	
	
	public void sendReq(char type) throws IOException {
	}
	
	
	public void release() {
		
	}	
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	

	

}
