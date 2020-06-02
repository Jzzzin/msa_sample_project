#FROM openjdk:8-jre

# Default port for DynamoDB Local
#EXPOSE 8000
#RUN mkdir /var/dynamodb_local && (wget -q -O - https://s3-us-west-2.amazonaws.com/dynamodb-local/dynamodb_local_latest.tar.gz | tar -xzf - )

# Default command for image
#CMD java ${JAVA_OPTS} -Djava.library.path=. -jar DynamoDBLocal.jar -dbPath /var/dynamodb_local -sharedDb -port 8000
FROM amazon/dynamodb-local:1.11.477
ENTRYPOINT java -jar DynamoDBLocal.jar -inMemory -sharedDb -port 8000
HEALTHCHECK --start-period=5s --interval=5s CMD curl http://localhost:8000 || exit 1