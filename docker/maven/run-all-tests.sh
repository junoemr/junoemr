#!/bin/bash

mkdir -p ~/.npm.docker
mkdir -p ~/.m2

DOCKER_USER="$(id -u):$(id -g)" USER_HOME="$HOME" sudo -E docker-compose -f ${BASH_SOURCE%/*}/docker-compose.yml up --exit-code-from juno-maven
