FROM eclipse-temurin:17-jre

EXPOSE 4001
RUN mkdir /app

COPY build/libs/federation-compatibility.jar /app/app.jar

ENTRYPOINT ["java", "-jar","/app/app.jar"]
