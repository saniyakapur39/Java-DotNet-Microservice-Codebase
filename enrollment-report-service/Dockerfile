FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY target/enrollment-report-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]