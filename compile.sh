rm -r bin
mkdir bin
javac -d bin -sourcepath src -cp hadoop-common-2.7.2.jar:hadoop-mapreduce-client-core-2.7.2.jar src/*.java src/*/*.java
rm out.jar
jar -cvf out.jar -C bin .
