FROM amazoncorretto:17-alpine
VOLUME /tmp
ARG JAR_FILE=build/libs/gestion-contact-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
