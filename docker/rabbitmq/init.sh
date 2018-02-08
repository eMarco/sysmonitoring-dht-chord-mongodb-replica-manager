#!/bin/bash
EXCHANGE="amq.topic"
SCANNER_PASS="somepass"
HANDLER_PASS="somepass"

/etc/init.d/rabbitmq-server start

# Check how many scanner replicas exist
function init() {
    echo "Init script launched..."
    echo "Looking for number of replicas"
    REPLICAS=1

    # Loop until DNS resolution of common_name_$REPLICA_ID fails
    while [ $(host -4 -t a distsystems_scanner_$REPLICAS | grep "not found" > /dev/null; echo $?) -ne 0 ]
    do
        ((REPLICAS++))
    done
    echo "Found $REPLICAS replica(s)."

    # Create a new user for each scanner replica
    for i in `seq 1 $REPLICAS`;
    do
        # Scanners
        rabbitmqctl add_user distsystems_scanner_$i $SCANNER_PASS
        # Do not allow user to edit config and r\w other resources
        rabbitmqctl set_permissions -p / distsystems_scanner_$i "a^" "$EXCHANGE" "a^"
        # set_topic_permissions [-p <vhost>] <username> <exchange> <write_pattern> <read_pattern>
        rabbitmqctl set_topic_permissions -p / distsystems_scanner_$i "$EXCHANGE" "distsystems_scanner_$i\.monitor\.[\w]*" "distsystems_scanner_$i\.monitor\.[\w]*"
    done

    # Message handler 
    rabbitmqctl add_user message_handler $HANDLER_PASS
    rabbitmqctl set_permissions -p / message_handler ".*" ".*" ".*"
    rabbitmqctl set_topic_permissions -p / message_handler "$EXCHANGE" "a^" "[\w]*\.monitor\.[\w]*"
}

init > /var/log/init.log &

tail -f /var/log/init.log &
tail -f /var/log/rabbitmq/rabbit1.log

