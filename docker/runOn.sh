#!/bin/sh
if [ $# -ne 2 ]; then
    echo "Usage: ./runOn.sh <container_name> \"<command>\""
    echo "Example ./runOn.sh scanner \"bash scanner.sh\""
    exit
fi
for i in $(docker-compose ps -q $1)
do
    docker exec -it $i $2
done
