#! /bin/bash

usage()
{

echo "Usage: sh $0 <program-id> <input-hdfs-file> <output-hdfs-path>"
exit 1;


}


if [ $# -eq 3 ]
then

pgm_id=$1 
JAR=/home/rajjanwa/workspace/rmj-spark-kafka-pgm/target/scala-2.11/rmj-spark-kafka-pgm-assembly-1.0.jar

case $pgm_id in
"PGM1")
spark-submit --master yarn --deploy-mode cluster  --executor-memory 1G  --num-executors 2 --class com.rmj.spark.kafka.SparkPgM $JAR hdfs://rmj:9000$2 hdfs://rmj:9000$3
rc=$?
;;

*)
usage
;;
esac
if [ $rc -eq 0 ]
then
echo "Job run success"
else
echo "Job Failed with Exit code : $rc"
fi

else

usage

fi
