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

12. 쿠버네티스 실행 시 docker 쿠버네티스 가동해야 됨

13. 쿠버네티스 디플로이먼트 apiVersion: apps/v1 로 변경해야 됨

14. 쿠버네티스 디플로이먼트에 셀렉터 추가해야됨
  selector: # 디플로이먼트에 셀렉터 추가해야 됨
    matchLabels:
      svc: ftgo-restaurant-service

15. 쿠버네티스 statefulSet 에 셀렉터 추가해야 됨

16. 쿠버네티스 job 수정하는 경우 기존 job을 지워야 한다
kubectl delete jobs/ftgo-dynamodb-local-init

17. 쿠버네티스 kafka 실행하기 위해 image 버전을 latest로 변경 
>>> kafka, zookeeper 버전이 달라져서 변경 테스트 중

18. sh 스크립트를 batch 스크립트로 일일이 변환하는데 한계가 있어서 wsl ubuntu 에서 실행하기로 결정
>>> wsl version 1 에서 쿠버네티스 실행이 안되서 wsl version 2로 업그레이드 
>>> wsl version 2 에서 도커 환경 설정 값이 달라짐 
>>> Expose daemon on tcp://localhost:2375 사용하지 않고 
Use the WSL 2 based engine 사용하고 WSL INTEGRATION enable 해야됨
>>> DOCKER_HOST 환경변수 설정하면 접속 못함
>>> DOCKER_HOST_IP=$(ip route get 8.8.8.8 | awk '{print $(NF-2); exit}') 로 설정해야됨

19. wsl2 ubuntu 에서 gradle docker-compose plugin 으로 component test 실행 시
network 대역 문제로 tcp port open wait 하면 진행되지 않으므로 해당 옵션을 false 로 변경 
        // skip waiting for tcp port open
        waitForTcpPorts = false

20. 쿠버네티스 스크립트 실행 순서 
./kubernetes-deploy-and-test.sh
./kubernetes-kill-port-forwarding.sh
./kubernetes-delete-all.sh
./kubernetes-delete-volumes.sh

21. 쿠버네티스 end to end test 실행 시 docker-compose 와 cdc-service 구성이 다르므로
docker-compose 구성으로 맞춘다
1) ftgo-cdc-service.yml 로 cdc 서비스 구동함
2) 각 서비스가 각자의 DB에 접속하도록 yml 파일 변경
          - name: SPRING_DATASOURCE_URL
            value: jdbc:mysql://ftgo-mysql/ftgo_consumer_service
          - name: SPRING_DATASOURCE_USERNAME
            value: ftgo_consumer_service_user
          - name: SPRING_DATASOURCE_PASSWORD
            value: ftgo_consumer_service_password
          - name: EVENTUATE_DATABASE_SCHEMA
            value: ftgo_consumer_service

22. 윈도우와 우분투에서 번갈아 테스트 하다보면 에러가 나는 경우가 있으므로 
그때는 컴파일부터 다시 실행해본다
./gradlew assemble
./docker-compose build
DOCKER_USER_ID=??? DOCKER_PASSWORD=??? ./publish-docker-image.sh
20번 순서대로 실행
