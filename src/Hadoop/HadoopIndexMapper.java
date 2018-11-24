package Hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

import SearchEngine.TokenScanner;

public class HadoopIndexMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws InterruptedException {

        String line = value.toString();
        String valuestr = ((FileSplit) context.getInputSplit()).getPath().getName();
        Text documentName = new Text(valuestr);
        int airTemperature;
        TokenScanner tokenizer= new TokenScanner(line);
        String word = tokenizer.getNextToken();
        while (!word.equals("")){
            try{
                context.write(new Text(word), documentName);
            } catch(IOException exp){
                exp.printStackTrace();
            }
        }
    }
}