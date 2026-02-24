FROM eclipse-temurin:17-jre
EXPOSE 8080

ARG JAR_FILE=build/libs/products-subgraph.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
