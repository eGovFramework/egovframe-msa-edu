# docker run --name egov-config -d -p 8888:8888 -e profile=prod -e ENCRYPT_KEY=??? jaeyeolkim/egov-config
# base image - openjdk8
FROM openjdk:8-jre-alpine
# jar 파일이 복사되는 위치
ENV APP_HOME=/usr/app/
# 작업 시작 위치
WORKDIR $APP_HOME
# jar 파일 복사
COPY build/libs/*.jar config.jar
# application port
EXPOSE 8888
# 실행 (ARG 아니고 ENV 값이다)
CMD ["java", "-Dspring.profiles.active=${profile:prod}", "-jar", "config.jar"]
