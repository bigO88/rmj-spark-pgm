#! /bin/bash

# Note: use
# [cloudera@quickstart ~]$ hdfs getconf -confKey fs.defaultFS
#      hdfs://quickstart.cloudera:8020

usage()
{

echo "Usage: sh $0 <program-id> <input-hdfs-file> <output-hdfs-path>"
echo " Options:"
echo "   1.) Program Id: PairedRddActionsByKey|SparkCassandraConnectExample"
exit 1;
}


if [ $# -eq 3 ]
then

pgm_id=$1 
JAR=/data/rmj-spark-pgm/target/scala-2.11/assembly-spark-rmj-project.jar

case $pgm_id in
"PairedRddActionsByKey")
spark-submit --master yarn --deploy-mode cluster  --executor-memory 1G  --num-executors 2 --class com.rmj.spark.cassandra.PairedRddActionsByKey $JAR hdfs://quickstart.cloudera:8020$2 hdfs://quickstart.cloudera:8020$3
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

