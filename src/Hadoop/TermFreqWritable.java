package Hadoop;

import java.io.*;
import org.apache.hadoop.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TermFreqWritable implements WritableComparable<TermFreqWritable> {

	private Text term;
	private IntWritable freq;

	// Default Constructor
	public TermFreqWritable() {
		this.term = new Text();
		this.freq = new IntWritable();
	}

	// Custom Constructor
	public TermFreqWritable(Text term, IntWritable freq) {
		this.term = term;
		this.freq = freq;
	}

	// Setter method to set the values of WebLogWritable object
	public void set(Text term, IntWritable freq) {
		this.term = term;
		this.freq = freq;
	}

	public Text getTerm() {
		return term;
	}


	public IntWritable getFreq() {
		return freq;
	}

	@Override
	// overriding default readFields method.
	// It de-serializes the byte stream data
	public void readFields(DataInput in) throws IOException {
		term.readFields(in);
		freq.readFields(in);
	}

	@Override
	// It serializes object data into byte stream data
	public void write(DataOutput out) throws IOException {
		term.write(out);
		freq.write(out);
	}

	@Override
	public int compareTo(TermFreqWritable o) {
		if (term.compareTo(o.term) == 0) {
			return (freq.compareTo(o.freq));
		} else
			return (term.compareTo(o.term));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TermFreqWritable) {
			TermFreqWritable other = (TermFreqWritable) o;
			return term.equals(other.term) && freq.equals(other.freq);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (term).hashCode();
	}
}