#!  /bin/bash


usage()
{
echo "Please choose options:"
echo " 1.) Run cloudera-quickstart docker image with docker-shared directory"
echo " 2.) Attach cloudera-quickstart running container"
echo " 3.) Stop cloudera-quickstart docker container"
exit 1;
}

if [ $# -eq 1 ]
then

case $1 in
1)  

echo "Cloudera QuickStart docker starting....."
docker run --hostname=quickstart.cloudera --privileged=true -d -t -i -p 8888 -p 8080 cloudera/quickstart /usr/bin/docker-quickstart -v /home/rajjanwa/docker-shared:/data
rc=$?
if [ $rc -eq 0 ]
then
echo "Cloudera quickstart docker container started"
else
echo "Cloudera quickstart docker container failed to start. Please check logs"
fi
;;

2)
pid=`docker ps|grep quickstart`
if [ $? -eq 0 ]
then
container_id=`docker ps|grep quickstart|awk '{print $1}'`
docker attach $container_id
rc=$?

if [ $rc -eq 0 ]
then
echo "Cloudera quickstart container attached"
else
echo "Cloudera quickstart container failed to attached.Please check"
fi




else
echo "No cloudera quickstart container running."
fi

;;

3)
pid=`docker ps|grep quickstart`
if [ $? -eq 0 ]
then
container_id=`docker ps|grep quickstart|awk '{print $1}'`
docker stop $container_id
rc=$?

if [ $rc -eq 0 ]
then
echo "Cloudera quickstart container stopped"
else
echo "Cloudera quickstart container failed to stop.Please check"
fi
else
echo "No cloudera quickstart container running."
fi
;;
esac

else
usage
fi
