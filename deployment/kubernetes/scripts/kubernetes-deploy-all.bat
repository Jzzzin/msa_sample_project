kubectl apply -k ../stateful-services/

call ./kubernetes-wait-for-ready-pods.bat ftgo-mysql-0 ftgo-kafka-0 ftgo-dynamodb-local-0 ftgo-zookeeper-0

kubectl apply -k ../cdc-services/

kubectl apply -k ../../../
