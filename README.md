1. 윈도우 환경에서 도커 환경 변수 세팅을 위해 set-env.bat 실행한다

2. intellij docker 플러그인으로 docker-compose 를 실행하면 환경변수가 전달되지 않으므로 
터미널에서 docker-compose up -d 로 도커를 실행한다

3. docker image 를 다시 빌드해야 될 경우 docker-compose build 로 이미지를 다시 빌드한다

4. dynamodblocal 의 경우 java 7 버전에서 작동하지 않으므로 Dockerfile 에서 
FROM openjdk:8-jre
으로 변경하고 
CMD java ${JAVA_OPTS} -Djava.library.path=. -jar DynamoDBLocal.jar -dbPath /var/dynamodb_local -sharedDb -port 8000
으로 변경한다

5. docker-compose 전에 gradlew assemble 로 jar 빌드한다