FROM amazoncorretto:21-alpine

WORKDIR /app

COPY build/libs/yechef-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]