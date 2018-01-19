#!/bin/sh
docker exec -t $(docker-compose ps -q bastion) $1
