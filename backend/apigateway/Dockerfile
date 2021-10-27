# openjdk8 base image
FROM openjdk:8-jre-alpine

# directory 생성
RUN mkdir -p /usr/app/msa-attach-volume/messages
# jar 파일이 복사되는 위치
ENV APP_HOME=/usr/app/
# 작업 시작 위치
WORKDIR $APP_HOME
# jar 파일 복사
COPY build/libs/*.jar apigateway.jar
# application port
EXPOSE 8000
# 실행
CMD ["java", "-Dspring.profiles.active=${profile:default}", "-jar", "apigateway.jar"]
