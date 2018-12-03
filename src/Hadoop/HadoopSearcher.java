package Hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HadoopSearcher {
	public static void main(String[] args) throws Exception {
	    if (args.length != 3) {
	      System.err.println("Usage: MaxTemperature <input path> <output path>");
	      System.exit(-1);
	    }
	    
	    Configuration conf = new Configuration();
	    conf.set("query", args[2]);
	    Job job = Job.getInstance(conf);
	    job.setJarByClass(Hadoop.HadoopSearcher.class); 
	    job.setJobName("Searching Documents");

	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    
	    job.setMapperClass(HadoopSearcherMapper.class);
	    job.setReducerClass(HadoopSearcherReducer.class);

	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
