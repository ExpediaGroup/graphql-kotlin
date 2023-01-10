FROM gradle:7.6.0-jdk17

EXPOSE 4001
RUN mkdir /app

COPY build/libs/federation-compatibility.jar /app/app.jar

ENTRYPOINT ["java", "-jar","/app/app.jar"]
