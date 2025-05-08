FROM gradle:8-jdk21 AS builder
WORKDIR /home/gradle/project

COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew clean build --no-daemon -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/yechef-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
