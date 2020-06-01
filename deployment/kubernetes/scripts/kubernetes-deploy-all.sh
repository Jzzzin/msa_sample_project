#! /bin/bash -e

kubectl apply -k ../stateful-services/

./kubernetes-wait-for-ready-pods.sh ftgo-mysql-0 ftgo-kafka-0 ftgo-dynamodb-local-0 ftgo-zookeeper-0

kubectl apply -k ../cdc-services/

kubectl apply -k ../../../
