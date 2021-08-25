FROM adoptopenjdk:16.0.1_9-jre-hotspot-focal as builder
WORKDIR /workspace
ARG WAR_FILE=./target/oscar-14.0.0-SNAPSHOT.war
COPY ${WAR_FILE} juno.war
RUN java -Djarmode=layertools -jar juno.war extract

FROM adoptopenjdk:16.0.1_9-jre-hotspot-focal
RUN apt update -y && apt install -y wget xfonts-75dpi ghostscript openssl vim
RUN wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.6-1/wkhtmltox_0.12.6-1.focal_amd64.deb
RUN apt install -y ./wkhtmltox_0.12.6-1.focal_amd64.deb
WORKDIR /workspace
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.WarLauncher"]
