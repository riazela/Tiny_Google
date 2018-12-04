import SearchEngine.TokenScanner;

public class test {
    public static void main(String[] args){
    	String query = "temperatures";
		String[] queryParts = (new TokenScanner(query)).getAllTokens();
		query = "";
		for (int i = 0; i < queryParts.length; i++) {
			query = query+queryParts[i]+" ";
		}
		System.out.println(query.replaceAll("temperatures" + "\\s", "").equals(""));
		System.out.println(query.toString());
		System.out.println(query.replaceAll("temperatures" + "\\s", "").toString());
    }
}
