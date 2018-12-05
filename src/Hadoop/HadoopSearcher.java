package Hadoop;

import java.util.Date;

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
	      System.err.println("Usage: HadoopSearcher <index path> <output path> <query>");
	      System.exit(-1);
	    }
	    Date date = (new Date());
	    long time =  (date).getTime();
	    
	    Configuration conf = new Configuration();
	    conf.set("query", args[2]);
	    Job job = Job.getInstance(conf);
	    job.setJarByClass(Hadoop.HadoopSearcher.class); 
	    job.setJobName("Searching Documents");

	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    
	    job.setMapperClass(HadoopSearcherMapper.class);
	    job.setReducerClass(HadoopSearcherReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(TermFreqWritable.class);
	    
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    
	    int res = job.waitForCompletion(true) ? 0 : 1;
	    System.out.println(date.getTime()-time);
	    System.exit(res);
	}
}
