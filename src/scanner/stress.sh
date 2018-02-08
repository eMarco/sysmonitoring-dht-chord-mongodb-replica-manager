#!/bin/bash
echo "[Scanner Entrypoint] Up and running"
/etc/init.d/cron restart

echo "Looking for my replica ID"
MYIP=$(ip addr show eth0 | grep inet[^6] | sed 's/.*inet \(.*\)\/[0-9]* \(.* \)*scope.*/\1/')
REPLICA_ID=1
PREFIX=distsystems_scanner

# Loop until DNS resolution of common_name_$REPLICA_ID return $MYIP
while [ "$(host -4 -t a ${PREFIX}_${REPLICA_ID} | cut -d " " -f 4)" != "$MYIP" ]
do
    ((REPLICA_ID++))
done
echo "I am distsystems_scanner_$REPLICA_ID."

echo "${PREFIX}_${REPLICA_ID}" > /var/run/replica_id

while true
do
    echo "Stress me..."
    sleep 60
done
