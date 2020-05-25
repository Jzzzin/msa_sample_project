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

6. contract 테스트 전에 contracts 모듈에서 gradlew publish 해서 stubs.jar를 생성해야 한다

7. 별도의 test sourceSet 디렉토리를 사용하는 경우 gradle 에서 idea 플러그인으로 
modules 에 testSourceDirs += file("${projectDir}/src/integration-test/java") 처럼 
테스트 소스 디렉토리를 추가해야 인텔리J에서 바로 테스트 실행이 가능하다

8. gradle 에서 sourceSet 을 여러개 구성하여 사용하는 경우 gradle setting 에서 
create separate module per sourceset 을 체크하자

9. component test는 gradlew :ftgo-order-service:componentTest 로 실행하면
dockerCompose를 실행시킨뒤 테스트 진행 한다.

10. component test 후 docker-compose down -v 로 도커를 내려야 teardown 된다

11. end to end test는 gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test 로 실행한다
