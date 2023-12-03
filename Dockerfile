FROM maven:3.9.5-eclipse-temurin-8 as build

RUN mkdir /opt/app
WORKDIR /opt/app

COPY local_repo local_repo
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
COPY utils utils
COPY .git .git

COPY fix_openapi_generate_hack.sh .

COPY build.sh .
RUN ./build.sh