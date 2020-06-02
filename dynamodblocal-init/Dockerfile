#FROM ubuntu:18.04
#RUN apt-get update && apt-get install -y python && apt-get install -y wget && wget https://bootstrap.pypa.io/get-pip.py && python get-pip.py
#RUN pip install awscli --upgrade
#COPY create-dynamodb-tables.sh .
#COPY ftgo-order-history.json .
#COPY wait-for-dynamodblocal.sh .
#CMD ./wait-for-dynamodblocal.sh && ./create-dynamodb-tables.sh
FROM python:3.6.10-alpine3.11
RUN pip install awscli --upgrade
COPY create-dynamodb-tables.sh .
COPY ftgo-order-history.json .
COPY wait-for-dynamodblocal.sh .
RUN chmod +x *.sh
HEALTHCHECK --interval=10s --retries=10 --timeout=3s CMD [[ -f /tables-created ]]

CMD ./wait-for-dynamodblocal.sh && ./create-dynamodb-tables.sh