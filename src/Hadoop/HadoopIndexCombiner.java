package Hadoop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class HadoopIndexCombiner extends Reducer<Text, TermFreqWritable, Text, TermFreqWritable> {

	@Override
	public void reduce(Text word, Iterable<TermFreqWritable> values, Context context)
			throws IOException, InterruptedException {

		HashMap<String, Integer> counter = new HashMap();

		TermFreqWritable lastValue = null;
		for (TermFreqWritable value : values) {
			if (lastValue != null) {
				if (lastValue.getTerm().equals(value.getTerm())) {
					value.getFreq().set(value.getFreq().get() + lastValue.getFreq().get());
				} else {
					context.write(word, lastValue);
				}
			}
			lastValue = value;
		}

		if (lastValue != null) {
			context.write(word, lastValue);
		}

	}
}
