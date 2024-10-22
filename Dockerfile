#
# (c) Copyright 2024-2024, ANS. All rights reserved.
#

ARG VERSION

FROM openjdk:17-jdk-slim-buster
COPY ./ruleschecker/target/check-apim-rules-ruleschecker-*.jar /usr/app/apim-ruleschecker.jar
RUN chmod +x /usr/app/apim-ruleschecker.jar
USER daemon
ENTRYPOINT ["java","-jar","/usr/app/apim-ruleschecker.jar"]
CMD ["--envid=DEFAULT", "--apikey=apikey", "--recipients.filepath=filepath", "--email.sender=noreply@esante.gouv.fr"]