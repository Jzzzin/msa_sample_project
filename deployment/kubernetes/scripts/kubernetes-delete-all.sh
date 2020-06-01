#! /bin/bash -e

kubectl delete -k ../stateful-services

kubectl delete -k ../cdc-services

kubectl delete -k ../../../