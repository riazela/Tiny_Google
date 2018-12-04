package Hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import SearchEngine.TokenScanner;

public class HadoopSearcherReducer extends Reducer<Text, TermFreqWritable, Text, IntWritable> {

	  @Override
	  public void reduce(Text key, Iterable<TermFreqWritable> allTermDocs,
	      Context context)
	      throws IOException, InterruptedException {
		
		String query = context.getConfiguration().get("query").toLowerCase();
		String[] queryParts = (new TokenScanner(query)).getAllTokens();
		query = "";
		for (int i = 0; i < queryParts.length; i++) {
			query = query+queryParts[i]+" ";
		}
		
		int score = 0;
		for (TermFreqWritable termDocFreqWritable : allTermDocs) {
			String term = termDocFreqWritable.getTerm().toString();
			query = query.replaceAll(term+"\\s", "");
			score += termDocFreqWritable.getFreq().get();
		}
	    
	    if (query.equals("")) {
	    	context.write(key, new IntWritable(score));
	    } 
	    
	  }
	}

