package Hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import SearchEngine.TokenScanner;

public class HadoopSearcherMapper  extends Mapper<LongWritable, Text, Text, TermFreqWritable> {

    @Override
    public void map(LongWritable key, Text indexFile, Context context) throws InterruptedException, IOException {
    	String query = context.getConfiguration().get("query").toLowerCase();
    	String[] queryParts = (new TokenScanner(query)).getAllTokens();
    	
        String line = indexFile.toString();
        String[] lineparts = line.split("\\s+");
        String term = lineparts[0];
        
        int queryOccurance = 0;
        
        for (int i = 0; i < queryParts.length; i++) {
			if (queryParts[i].equals(term))
				queryOccurance++;
		}
        
        Text termtext = new Text(term);
        
        if (queryOccurance >0) {
        	System.out.println("found a term "+term);
        	for (int i = 1; i < lineparts.length; i++) {
        		String[] t = lineparts[i].split(":");
        		String docname = t[0];
        		int freq = Integer.parseInt(t[1]);
        		freq = freq*queryOccurance;
        		context.write(new Text(docname), new TermFreqWritable(termtext, new IntWritable(freq)));
			}
        }
        
    }
}