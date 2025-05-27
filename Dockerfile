FROM openjdk:17-jdk-alpine

COPY target/clothes-0.0.1-SNAPSHOT.jar clothes-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "clothes-0.0.1-SNAPSHOT.jar"]
