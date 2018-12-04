hadoop fs -rm -r searchresult
hadoop jar out.jar Hadoop.HadoopSearcher index searchresult "$1"
rm -r searchresult
hadoop fs -get searchresult .
cat searchresult/*
