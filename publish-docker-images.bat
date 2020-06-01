
SETLOCAL ENABLEDELAYEDEXPANSION
set "SERVICE_LIST=ftgo-consumer-service ftgo-order-service ftgo-kitchen-service ftgo-restaurant-service ftgo-accounting-service ftgo-order-history-service ftgo-api-gateway dynamodblocal-init mysql"

docker login -u ??? -p ???

for %%i in (%SERVICE_LIST%) do (
    docker tag msa-study_%%i jzzzin^/%%i^:latest
    docker push jzzzin^/%%i^:latest
)
