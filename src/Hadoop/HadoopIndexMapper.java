package Hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.Mapper;

import SearchEngine.TokenScanner;

public class HadoopIndexMapper extends Mapper<LongWritable, Text, Text, TermFreqWritable> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {

        String line = value.toString();
        String valuestr = ((FileSplit) context.getInputSplit()).getPath().getName();
        Text documentName = new Text(valuestr);
        int airTemperature;
        TokenScanner tokenizer= new TokenScanner(line);
        String word = tokenizer.getNextToken();
        Text wordText = new Text();
        TermFreqWritable termFreqWritable = new TermFreqWritable(documentName, new IntWritable(1));
        while (!word.equals("")){
        	wordText.set(word);
        	context.write(wordText, termFreqWritable);
	        word = tokenizer.getNextToken();
        }
    }
}