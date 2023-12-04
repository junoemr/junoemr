FROM maven:3.9.5-eclipse-temurin-8 as build

RUN apt-get update && apt-get -qq -y install tomcat9 && rm -rf /var/lib/apt/lists/*
ENV CATALINA_HOME=/usr/share/tomcat9

RUN mkdir /opt/app
WORKDIR /opt/app

COPY local_repo local_repo
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
COPY utils utils
COPY .git .git

COPY fix_openapi_generate_hack.sh jspc.xml ./

COPY build.sh .
RUN ./build.sh