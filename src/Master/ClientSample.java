package Master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientSample {
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {
		
		long startTime = 0;
		long endTime = 0;
		long timeElapsed = 0;

		if(args.length < 1) {
			System.out.println("Not enough args!");
			return;
		}

		String[] ipPort = args[0].split(":");
		Socket socket = new Socket(ipPort[0],Integer.parseInt(ipPort[1]));
		OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream()); 
		BufferedReader in = new BufferedReader( 
				new InputStreamReader(socket.getInputStream())); 
		
		System.out.println("Client started on port " + ipPort[1] );

		boolean closed = false;
		
		while(!closed) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter command (indexdir/indexdoc/search): ");
			String cmd = scanner.nextLine() + "\n";
			String[] cmdParts = cmd.split(" ");
			String str = " ";				
			startTime = System.currentTimeMillis();
			if (cmdParts[0].equals("indexdir")) {
				if(cmdParts.length < 2) {
					System.err.println("Not enough arguments!");
					out.write("close");
					out.flush();
					socket.close();
					System.out.println("Client closed!");
					return;
				}
				out.write(cmd);
				out.flush();
				while(!str.equals("")) {
					str = in.readLine();
					System.out.println(str);
				}
			}
			else if (cmdParts[0].equals("indexdoc"))
			{
				if(cmdParts.length < 2) {
					System.err.println("Not enough arguments!");
					out.write("close");
					out.flush();
					socket.close();
					System.out.println("Client closed!");
					return;
				}
				out.write(cmd);
				out.flush();
				while(!str.equals("")) {
					str = in.readLine();
					System.out.println(str);
				}
				
			}
			else if (cmdParts[0].equals("search"))
			{
				if(cmdParts.length < 2) {
					System.err.println("Not enough arguments!");
					out.write("close");
					out.flush();
					socket.close();
					System.out.println("Client closed!");
					return;
				}
				out.write(cmd);
				out.flush();
				while(!str.equals("")) {
					str = in.readLine();
					System.out.println(str);
				}
				
			}
			else if (cmdParts[0].equals("close"))
			{
				closed = true;				
			}
			else if (cmdParts[0].equals("reset"))
			{
				closed = true;		
				out.write(cmd);
				out.flush();
				while(!str.equals("")) {
					str = in.readLine();
					System.out.println(str);
				}
			}else if (cmdParts[0].equals("save"))
			{
				closed = true;		
				out.write(cmd);
				out.flush();
				while(!str.equals("")) {
					str = in.readLine();
					System.out.println(str);
				}
			}
			else if (cmdParts[0].equals("laod"))
			{
				closed = true;		
				out.write(cmd);
				out.flush();
				while(!str.equals("")) {
					str = in.readLine();
					System.out.println(str);
				}
			}
			endTime = System.currentTimeMillis();
			timeElapsed = endTime - startTime;
			System.out.println("Response time (ms): " + timeElapsed);
			
		}
		
		out.write("close");
		out.flush();
		socket.close();
		System.out.println("Client closed on port "  + ipPort[1] );
		
	}
}

