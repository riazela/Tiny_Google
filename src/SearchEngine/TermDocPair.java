package SearchEngine;

public class TermDocPair implements Comparable<TermDocPair>{
	String term;
	int doc;
	int freq;
	public TermDocPair(String term, int doc, int freq) {
		super();
		this.term = term;
		this.doc = doc;
		this.freq = freq;
	}
	
	public boolean merge(TermDocPair t) {
		if (t.term.equals(this.term)) {
			this.freq += t.freq;
			return true;
		}
		return false;
	}
	
	@Override
	public int compareTo(TermDocPair o) {
		return this.term.compareTo(o.term);
	}
	
	
	
}
