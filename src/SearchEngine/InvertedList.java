package SearchEngine;

import java.util.Hashtable;
import java.util.LinkedList;

public class InvertedList {
	Hashtable<String, LinkedList<DocFreq>> lookupTable;
	
	public InvertedList() {
		lookupTable= new Hashtable<>();
	}
	
	public void addTermDocPair(TermDocPair p) {
		if (!lookupTable.containsKey(p.term)) {
			lookupTable.put(p.term, new LinkedList<>());
		}
		lookupTable.get(p.term).add(new DocFreq(p.doc, p.freq));
	}
	
	public LinkedList<DocFreq> getListOf(String term) {
		return lookupTable.getOrDefault(term, new LinkedList<>());
	}
}
