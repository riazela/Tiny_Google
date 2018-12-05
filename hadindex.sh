hadoop fs -rmr index
hadoop jar out.jar Hadoop.HadoopIndexer input index
rm -r index
hadoop fs -get index .
