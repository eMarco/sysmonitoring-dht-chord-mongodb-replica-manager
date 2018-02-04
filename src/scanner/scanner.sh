#!/bin/bash
# A very nice esotheric scanner (Dependencies: sysstat***, gnu-coreutils, perl, awk, iproute, procps)

TOPIC_PREFIX=$(</var/run/replica_id)
RABBIT_USER=$TOPIC_PREFIX
RABBIT_PASS="somepass"
EXCHANGE="amq.topic"

# Pretty print the date using in seconds since the epoch
function get_timestamp {
    echo "\"timestamp\": $(date +%s)"
}

function get_uptime {
    uptime=$(</proc/uptime)
    uptime=${uptime%%.*}
    seconds=$((uptime%60))
    minutes=$((uptime/60%60))
    hours=$((uptime/60/60%24))
    days=$((uptime/60/60/24))

    echo "{\"seconds\": \"$seconds\", \"minutes\":\"$minutes\", \"hours\":\"$hours\", \"days\": \"$days\", $(get_timestamp), \"className\": \"org.unict.ing.pds.dhtdb.utils.model.UptimeStat\"}"
}

function get_load {
    load=$(</proc/loadavg)
    echo "{\"load\": \"${load%% [0-9][/]*}\"}"
}

# Computes the sum of cpu usages for each process
function get_cpu_usage {
    ret=0
    for i in `ps -eo pcpu | tail -n +2`
    do
        ret=$(echo $ret $i | awk '{print $1 + $2}')
    done
    echo "{\"usage\": $ret, $(get_timestamp), \"className\": \"org.unict.ing.pds.dhtdb.utils.model.CPUStat\"}"
}

function get_mem_usage {
    echo "{" $(cat /proc/meminfo | head -n 3 | sed -e 's/\(.*\):[ ]*\([0-9]\+\) kB/"\1":"\2"/' -e 's/$/,/') $(get_timestamp), \"className\": \"org.unict.ing.pds.dhtdb.utils.model.RAMStat\" }
}

#TODO better way to not depend on sysstat
function get_io_stats {
    echo \[$(iostat | tail -n +7 | sed 's/[ ]\+/ /g' | cut -d " " -f 1,3,4 | sed "s/^\(.*\) \(.*\) \(.*\)/{\"disk\":\"\1\", \"WritekBps\":\"\2\", \"ReadkBps\":\"\3\", $(get_timestamp), \"className\": \"org.unict.ing.pds.dhtdb.utils.model.IOStat\" },/")#\] | sed 's/,#//'
}

function send {
    TOPIC=$1
    BODY=$2
    python3 /code/send.py --username=${RABBIT_USER} --password=${RABBIT_PASS} --exchange=${EXCHANGE} --routingkey="$TOPIC" --message="$BODY"
    echo -e "$TOPIC\t===>\t$BODY"
}

### Concurrent topic sends
send "$TOPIC_PREFIX.monitor.uptime" "$(get_uptime)" &
send "$TOPIC_PREFIX.monitor.memory" "$(get_mem_usage)" &
send "$TOPIC_PREFIX.monitor.cpu" "$(get_cpu_usage)" &
send "$TOPIC_PREFIX.monitor.ios" "$(get_io_stats)" &

sleep 5 # TODO DELETE when send will be implemented with rabbitmqadmin tool (just for tests

