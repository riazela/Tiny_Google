package Hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class HadoopIndexReducer
  extends Reducer<Text, TermFreqWritable, Text, Text> {

  @Override
  public void reduce(Text word, Iterable<TermFreqWritable> values,
      Context context)
      throws IOException, InterruptedException {

    HashMap<String, Integer> counter = new HashMap();
    
    for (TermFreqWritable value : values) {
        String doc = value.getTerm().toString();
        doc.replaceAll(":", "");
        Integer j = counter.get(doc);
        if (j == null)
        	j=0;
        counter.put(doc, j + value.getFreq().get());
    }
    
    //sorting the documents based on name
    String[] documentNames = counter.keySet().toArray(new String[0]);
    Arrays.sort(documentNames);
    String val = ""+documentNames[0]+":"+counter.get(documentNames[0]);
    for (int i = 1; i < documentNames.length; i++) {
    	val = val + " " + documentNames[i]+":"+counter.get(documentNames[i]);
    }
        context.write(word, new Text(val));
  }
}

