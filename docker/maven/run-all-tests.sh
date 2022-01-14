#!/bin/bash

DOCKER_USER="$(id -u):$(id -g)" docker-compose -f ${BASH_SOURCE%/*}/docker-compose.yml up --exit-code-from juno-maven
