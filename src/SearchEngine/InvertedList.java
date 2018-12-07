package SearchEngine;

import java.util.Hashtable;
import java.util.LinkedList;

public class InvertedList {
	Hashtable<String, LinkedList<DocFreq>> lookupTable;
	
	public InvertedList() {
		lookupTable= new Hashtable<String, LinkedList<DocFreq>>();
	}
	
	public void addTermDocPair(TermDocPair p) {
		if (!lookupTable.containsKey(p.term)) {
			lookupTable.put(p.term, new LinkedList<DocFreq>());
		}
		lookupTable.get(p.term).add(new DocFreq(p.doc, p.freq));
	}
	
	public LinkedList<DocFreq> getListOf(String term) {
		 LinkedList<DocFreq> l = lookupTable.get(term);
		 if (l==null)
			 return new LinkedList<DocFreq>();
		 else
			 return l;
	}
}
