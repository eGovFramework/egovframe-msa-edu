FROM openjdk:8-jre-alpine
WORKDIR /app/
COPY build/libs/*.jar app.jar
CMD ["java", "-Dlogstash.url=logstash.default.svc.cluster.local:8088", "-jar", "app.jar"]
