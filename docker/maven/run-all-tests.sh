#!/bin/bash

DOCKER_USER="$(id -u):$(id -g)" USER_HOME="$HOME" sudo -E docker-compose -f ${BASH_SOURCE%/*}/docker-compose.yml up --exit-code-from juno-maven
