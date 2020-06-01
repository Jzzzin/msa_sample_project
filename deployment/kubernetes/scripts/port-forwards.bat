SETLOCAL ENABLEDELAYEDEXPANSION

for /f "tokens=*" %%f in ('kubectl get pod -l application^=ftgo -o^=jsonpath^=^'{.items[*].metadata.name}^'') do call kubernetes-wait-for-ready-pods.bat %%f

call :doforward ftgo-accounting-service 8085 8080
call :doforward ftgo-consumer-service 8081 8080
call :doforward ftgo-api-gateway 8087 8080
call :doforward ftgo-order-service 8082 8080
call :doforward ftgo-restaurant-service 8084 8080
call :doforward ftgo-kitchen-service 8083 8080

goto :EOF

:doforward

for /f "tokens=*" %%i in ('kubectl get pods --selector^=svc^=%~1 -o^=jsonpath^=^'{.items[*].metadata.name}^'') do set pod=%%i&kubectl port-forward !pod:~1,-1! %~2:%~3 &
)

