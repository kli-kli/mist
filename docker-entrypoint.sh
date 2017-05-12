#!/bin/bash
set -e
cd ${MIST_HOME}

if [ "$1" = 'mist' ]; then
  if [ -e "configs/user.conf" ]; then
    cp -f configs/user.conf configs/docker.conf
  fi
  export IP=`ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'`
  echo "$IP    master" >> /etc/hosts
  exec ./bin/mist start master --config configs/docker.conf --java-args "-Dmist.akka.cluster.seed-nodes.0=akka.tcp://mist@$IP:2551 -Dmist.akka.remote.netty.tcp.hostname=$IP"
elif [ "$1" = 'worker' ]; then 
  if [ ! -z $5 ]; then
    echo $5 | base64 -d  > configs/docker.conf
  fi  
  export IP=`getent hosts master | awk '{ print $1 }'`
  export MYIP=`ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'`
  exec ./bin/worker --name $2 --context $3 --jar $4 --config configs/docker.conf --run-options "$6" --java-args "-Dmist.akka.cluster.seed-nodes.0=akka.tcp://mist@$IP:2551 -Dmist.akka.remote.netty.tcp.hostname=$MYIP -Dmist.akka.remote.netty.tcp.bind-hostname=$MYIP"
fi
