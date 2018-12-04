package Hadoop;

import java.util.Date;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HadoopIndexer {
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: HadoopIndexer <input path> <output path>");
      System.exit(-1);
    }
    Date date = (new Date());
    long time =  (date).getTime();
    Job job = Job.getInstance();
    job.setJarByClass(Hadoop.HadoopIndexer.class);
    job.setJobName("Indexing Documents");

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    job.setMapperClass(HadoopIndexMapper.class);
    job.setReducerClass(HadoopIndexReducer.class);
    
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(TermFreqWritable.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    int res = job.waitForCompletion(true) ? 0 : 1;
    System.out.println(date.getTime()-time);
    System.exit(res);
  }
}
