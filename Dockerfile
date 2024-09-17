ARG VERSION
FROM maven:3-openjdk-17 AS build
COPY settings-docker.xml /usr/share/maven/ref/
COPY pom.xml /usr/src/app/pom.xml
COPY gravitee-client/ /usr/src/app/gravitee-client/
COPY ruleschecker/ /usr/src/app/ruleschecker/
RUN ls /usr/src/app
RUN mvn -f /usr/src/app/pom.xml -DskipTests clean package

FROM openjdk:17-jdk-slim-buster
COPY --from=build /usr/src/app/ruleschecker/target/check-apim-rules-ruleschecker-*.jar /usr/app/apim-ruleschecker.jar
RUN chmod +x /usr/app/apim-ruleschecker.jar
USER daemon
ENTRYPOINT ["java","-jar","/usr/app/apim-ruleschecker.jar"]
CMD ["env", "dest"]