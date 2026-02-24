FROM eclipse-temurin:17-jre
EXPOSE 8081

ARG JAR_FILE=build/libs/reviews-subgraph.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
