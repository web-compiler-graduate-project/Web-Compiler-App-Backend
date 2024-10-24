FROM gradle:8.9-jdk21 AS build

WORKDIR /home/gradle/project

COPY . .

RUN chmod -R 755 /home/gradle/project

RUN gradle build -x test

FROM openjdk:22-jdk-slim

WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/*.jar /app/app.jar

EXPOSE 8084

ENV JAVA_OPTS=""

CMD ["java", "-jar", "/app/app.jar"]
