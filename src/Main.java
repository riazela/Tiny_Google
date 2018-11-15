import SearchEngine.TokenScanner;

public class Main {
	public static void main(String[] args) {
		TokenScanner tscanner = new TokenScanner("test.txt");
		String s = tscanner.getNextToken();
		while (s!="") {
			System.out.println(s);
			s=tscanner.getNextToken();
		}
		tscanner.closeFile();
	}
}
