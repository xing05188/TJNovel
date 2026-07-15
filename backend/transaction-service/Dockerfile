FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar
EXPOSE 7083
ENTRYPOINT ["java","-jar","/app.jar"]

