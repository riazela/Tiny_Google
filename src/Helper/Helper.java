package Helper;

import java.net.Socket;

public class Helper {
	Socket masterSocket;
	int ID;
	int port;
	
	
	
	public static void main(String[] args) {
		if (args.length != 1) {
		      System.err.println("Usage: Helper <Master Listening Port>");
		      System.exit(-1);
		    }
		int port = Integer.parseInt(args[0]);
	}
	
	/**
	 * This method waits for a master to connect and then creates an instance of Helper using its socket
	 * @param port
	 */
	public static void listenForMaster(int port) {
		
	}
	
	public Helper(Socket masterSocket) {
		//TODO: get our ID
		//TODO: get our port
		//TODO: get the list of other IP Addresses and their id and ports
		//TODO: listen to your own port 
		//TODO: connect to those helpers that have ID greater than us
	}
	
	
	public void someHelperConnected(Socket helperSocket) {
		
	}
}
