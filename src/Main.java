import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

import SearchEngine.TokenScanner;

public class Main {
	public static void main(String[] args) throws IOException {
		TokenScanner scann = new TokenScanner("alireza ali-reza alireza42@gmail.com https://google.com a:b:c;2-3-444-12;something \\some /thing (inside par) [inside bracket]");
		String s= scann.getNextToken();
		while (!s.equals("")) {
			System.out.println(s);
			s = scann.getNextToken();
		}
		scann.closeFile();
	}
}
