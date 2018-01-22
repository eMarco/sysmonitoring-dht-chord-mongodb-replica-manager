#!/bin/bash

# Check how many scanner replicas exist
echo "Init:" > /var/log/init.log
touch /var/log/rabbit.log

# Start rabbitmq
# rabbitmq-server > /var/log/rabbit.log 2>&1
/etc/init.d/rabbitmq-server start # >> /var/log/rabbit.log

echo "Looking for number of replicas" >> /var/log/init.log

REPLICAS=0

# Loop until DNS resolution of common_name_$REPLICA_ID fails
while [ $(host -4 -t a distsystems_scanner_$((REPLICAS+1)) > /dev/null; echo $?) -eq 0 ]
do
    ((REPLICAS++))
done
echo "Found $REPLICAS replica(s)." >> /var/log/init.log

# Create a new user for each scanner replica
for i in `seq 1 $REPLICAS`;
do

  # Scanners
  rabbitmqctl add_user distsystems_scanner_$i somepass >> /var/log/init.log

  #TODO move queue\exchange declaration somewhere else and remove the admin tag
  rabbitmqctl set_user_tags distsystems_scanner_$i administrator

                                    # -p vhost user conf write read
  # Do not allow user to edit config and r\w other queues
  rabbitmqctl set_permissions -p / distsystems_scanner_$i ".*" ".*" ".*"  >> /var/log/init.log # ".*" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log
  # rabbitmqctl set_permissions -p / distsystems_scanner_$i "monitor_queue" "amq.topic" "amq.topic"  >> /var/log/init.log # "distsystems_scanner_$i\.monitor\.[\w]*" "distsystems_scanner_$i\.monitor\.[\w]*" >> /var/log/init.log
              #set_topic_permissions [-p <vhost>] <username> <exchange> <write_pattern> <read_pattern>
  rabbitmqctl set_topic_permissions -p / distsystems_scanner_$i ".*a^" "distsystems_scanner_$i\.monitor\.[\w]*" "distsystems_scanner_$i\.monitor\.[\w]*" >> /var/log/init.log
done


# Declare monitor exchange
#rabbitmqadmin declare exchange name=my-new-exchange type=fanout


# Message handler
rabbitmqctl add_user message_handler somepass >> /var/log/init.log
#rabbitmqctl set_user_tags someuser administrator

# Do not allow MessageHandlers to edit config and write queues
                            # -p vhost user conf write read
# rabbitmqctl set_permissions -p / message_handler "monitor_queue" "amq.topic" "amq.topic"  >> /var/log/init.log # ".*" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log
rabbitmqctl set_permissions -p / message_handler ".*" ".*" ".*"  >> /var/log/init.log # ".*" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log

            #set_topic_permissions [-p <vhost>] <username> <exchange> <write_pattern> <read_pattern>
rabbitmqctl set_topic_permissions -p / message_handler ".*" "a^" "[\w]*\.monitor\.[\w]*" >> /var/log/init.log

tail -f /var/log/rabbit.log
