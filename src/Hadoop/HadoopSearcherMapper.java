package Hadoop;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import SearchEngine.TokenScanner;

public class HadoopSearcherMapper  extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text indexFile, Context context) throws InterruptedException, IOException {
    	String query = context.getConfiguration().get("query");
    	
        String line = indexFile.toString();
        String valuestr = ((FileSplit) context.getInputSplit()).getPath().getName();
        Text documentName = new Text(valuestr);
        int airTemperature;
        TokenScanner tokenizer= new TokenScanner(line);
        String word = tokenizer.getNextToken();
        while (!word.equals("")){
        	context.write(new Text(word), documentName);
	        word = tokenizer.getNextToken();
        }
    }
}