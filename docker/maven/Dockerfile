# syntax=docker/dockerfile:1
FROM maven:3.6.3-openjdk-8

RUN apt-get update && apt-get install -y default-mysql-client

#ENTRYPOINT ["mvn", "test"]
#ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]