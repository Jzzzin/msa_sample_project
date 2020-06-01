
kubectl delete -k ../stateful-services
kubectl delete -k ../cdc-services
kubectl delete -k ../../../

kubectl delete pods --all

kubectl delete pvc --all


