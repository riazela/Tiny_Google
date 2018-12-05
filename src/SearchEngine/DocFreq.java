package SearchEngine;

public class DocFreq implements Comparable<DocFreq> {
	public int docID;
	public int freq;
	public DocFreq(int docID, int freq) {
		super();
		this.docID = docID;
		this.freq = freq;
	}
	@Override
	public int compareTo(DocFreq o) {
		return this.docID-o.docID;
	}
	
	
}
