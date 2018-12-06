package Master;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import SearchEngine.DocFreq;
import SearchEngine.Indexer;
import SearchEngine.TermDocPair;
import SearchEngine.TokenScanner;

public class Main {
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1) {
			System.out.println("Not enough args!");
			return;
		}
		String path = args[0];
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		int clientPort = Integer.parseInt(fileScanner.nextLine());
		ArrayList<String> helpersAddress = new ArrayList<String>();
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			if (!line.equals(""))
				helpersAddress.add(line);
		}
		fileScanner.close();

		Master.connectToHelpers(helpersAddress.toArray(new String[0]));
		System.out.println("Master connected to helpers");
		
		if (!Master.getHelpersAck()) {
			System.out.println("Helpers setup failed!");
			return;
		}
		
		System.out.println("Master got setup Ack from helpers");
		
		Master.broadcast("connect");
		
		System.out.println("Master issued connect to helpers");
		
		if (!Master.getHelpersAck()) {
			System.out.println("Helpers connection failed!");
			return;
		}
		System.out.println("Master got connection Ack from helpers");
		
//		Master.issueIndex("docs/");
		
		Server.waitForClient(clientPort);
		

		
		
		
		
	}

}
