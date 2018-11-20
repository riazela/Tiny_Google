package SearchEngine;

public class TermDocPair implements Comparable<TermDocPair>{
	public String term;
	public int doc;
	public int freq;
	public TermDocPair(String term, int doc, int freq) {
		super();
		this.term = term;
		this.doc = doc;
		this.freq = freq;
	}
	
	public boolean merge(TermDocPair t) {
		if (t.term.equals(this.term) && t.doc==this.doc) {
			this.freq += t.freq;
			return true;
		}
		return false;
	}
	
	
	
	@Override
	public int compareTo(TermDocPair o) {
		if (this.doc==o.doc)
			return this.term.compareTo(o.term);
		else
			return this.doc-o.doc;
	}
	
	
	
}
