#!/bin/bash
EXCHANGE="amq.topic"
SCANNER_PASS="somepass"
HANDLER_PASS="somepass"

# Check how many scanner replicas exist
echo "Init:" > /var/log/init.log
touch /var/log/rabbit.log

# Start rabbitmq
# rabbitmq-server > /var/log/rabbit.log 2>&1
/etc/init.d/rabbitmq-server start # >> /var/log/rabbit.log

echo "Looking for number of replicas" >> /var/log/init.log

REPLICAS=0

# Loop until DNS resolution of common_name_$REPLICA_ID fails
while [ $(host -4 -t a distsystems_scanner_$((REPLICAS+1)) | grep "has no" > /dev/null; echo $?) -ne 0 ]
do
    ((REPLICAS++))
done
echo "Found $REPLICAS replica(s)." >> /var/log/init.log

# Create a new user for each scanner replica
for i in `seq 1 $REPLICAS`;
do

  # Scanners
  rabbitmqctl add_user distsystems_scanner_$i $SCANNER_PASS >> /var/log/init.log

                                    # -p vhost user conf write read
  # Do not allow user to edit config and r\w other resources
  rabbitmqctl set_permissions -p / distsystems_scanner_$i "a^" "$EXCHANGE" "a^"  >> /var/log/init.log # ".*" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log
              #set_topic_permissions [-p <vhost>] <username> <exchange> <write_pattern> <read_pattern>
  rabbitmqctl set_topic_permissions -p / distsystems_scanner_$i "$EXCHANGE" "distsystems_scanner_$i\.monitor\.[\w]*" "distsystems_scanner_$i\.monitor\.[\w]*" >> /var/log/init.log
done

# Message handler
rabbitmqctl add_user message_handler $HANDLER_PASS >> /var/log/init.log

                            # -p vhost user conf write read
rabbitmqctl set_permissions -p / message_handler ".*" ".*" ".*"  >> /var/log/init.log # ".*" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log

            #set_topic_permissions [-p <vhost>] <username> <exchange> <write_pattern> <read_pattern>
rabbitmqctl set_topic_permissions -p / message_handler "$EXCHANGE" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log

tail -f /var/log/rabbit.log
