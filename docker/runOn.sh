#!/bin/sh
if [ $# -ne 2 ]; then
    echo "Usage: ./runOn.sh <container_name> \"<command>\""
    echo "Example ./runOn.sh scanner \"bash scanner.sh\""
    exit
fi

docker exec -t $(docker-compose ps -q $1) $2
