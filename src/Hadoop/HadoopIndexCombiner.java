package Hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class HadoopIndexCombiner extends Reducer<Text, TermFreqWritable, Text, TermFreqWritable> {

	@Override
	public void reduce(Text word, Iterable<TermFreqWritable> values, Context context)
			throws IOException, InterruptedException {

		HashMap<String, Integer> counter = new HashMap();

		TermFreqWritable lastValue = null;
		int freq = 0;
		TermFreqWritable tfw = new TermFreqWritable();
		for (TermFreqWritable value : values) {
			if (lastValue != null) {
				if (lastValue.getTerm().equals(value.getTerm())) {
					freq += value.getFreq().get();
				} else {
					tfw.set(lastValue.getTerm().toString(), freq);
					context.write(word, tfw);
					freq = value.getFreq().get();
				}
			} else
			{
				freq = value.getFreq().get();
			}
			lastValue = value;
		}

		if (lastValue != null) {
			tfw.set(lastValue.getTerm().toString(), freq);
			context.write(word, tfw);
		}

	}
}
