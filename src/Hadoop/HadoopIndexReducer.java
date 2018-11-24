package Hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class HadoopIndexReducer
  extends Reducer<Text, Text, Text, Text> {

  @Override
  public void reduce(Text word, Iterable<Text> values,
      Context context)
      throws IOException, InterruptedException {

    HashMap<String, Integer> counter = new HashMap();
    
    for (Text value : values) {
        String doc = value.toString();
        doc.replaceAll(":", "");
        counter.put(doc, counter.getOrDefault(doc, new Integer(0)) + 1);
    }
    
    //sorting the documents based on name
    String[] documentNames = counter.keySet().toArray(new String[0]);
    Arrays.sort(documentNames);
    for (String docname:documentNames){
        context.write(word, new Text(""+docname+":"+counter.get(docname)));
    }
  }
}

