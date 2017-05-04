#!/bin/bash
source /etc/ecs/ecs.config
instanceArns=($(/usr/local/bin/aws ecs list-container-instances --cluster $ECS_CLUSTER | jq -r '.containerInstanceArns | .[]'))
peerIps=""
localIp=`ifconfig eth0 2>/dev/null|awk '/inet addr:/ {print $2}'|sed 's/addr://'`
for arn in "${instanceArns[@]}"
do
  instanceId=`/usr/local/bin/aws ecs describe-container-instances --cluster $ECS_CLUSTER --container-instances $arn | jq -r '.containerInstances | .[] | .ec2InstanceId'`
  instanceIp=`/usr/local/bin/aws ec2 describe-instances --instance-ids $instanceId | jq -r '.Reservations | .[] | .Instances | .[] | .PrivateIpAddress'`
  if [ "$localIp" != "$instanceIp" ] && [ "$instanceIp" != "null" ]; then
    peerIps="$peerIps $instanceIp"
  fi
done
echo $peerIps
