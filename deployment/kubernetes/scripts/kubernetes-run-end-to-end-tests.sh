#! /bin/bash

set -e

./kubernetes-wait-for-ready-pods.sh $(kubectl get pod -l application=ftgo  -o=jsonpath='{.items[*].metadata.name}')

./port-forwards.sh

cd ../../..

DOCKER_HOST_IP=localhost ./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test
