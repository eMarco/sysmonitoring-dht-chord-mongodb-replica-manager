#!/bin/bash
# A very nice esotheric scanner (Dependencies: sysstat***, gnu-coreutils, perl, awk, iproute, procps)

TOPIC_PREFIX=$(</var/run/replica_id)
RABBIT_USER=$TOPIC_PREFIX
RABBIT_PASS="somepass"

# Pretty print the date using rfc3339
function get_timestamp {
    echo "\"timestamp\": \"$(date --rfc-3339=ns)\""
}

function get_uptime {
    uptime=$(</proc/uptime)
    uptime=${uptime%%.*}
    seconds=$((uptime%60))
    minutes=$((uptime/60%60))
    hours=$((uptime/60/60%24))
    days=$((uptime/60/60/24))

    echo "{\"seconds\": \"$seconds\", \"minutes\":\"$minutes\", \"hours\":\"$hours\", \"days\": \"$days\", $(get_timestamp)}"
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
    echo "{\"cpu:\" \"$ret\", $(get_timestamp)}"
}

function get_mem_usage {
    echo "{" $(cat /proc/meminfo | head -n 3 | sed -e 's/\(.*\):[ ]*\([0-9]\+\) kB/"\1":"\2"/' -e 's/$/,/') $(get_timestamp)}
}

#TODO better way to not depend on sysstat
function get_io_stats {
    echo \[$(iostat | tail -n +7 | sed 's/[ ]\+/ /g' | cut -d " " -f 1,3,4 | sed 's/^\(.*\) \(.*\) \(.*\)/{"disk":"\1", "WritekBps":"\2", "ReadkBps":"\3"},/') {$(get_timestamp)}\]
}

function get_if_stats {
    echo $(ip -s -h link show | grep -E "RX|TX|^[0-9]*:" -A 1 | sed -e '/link/d' -e '/[RT]X/d' -e 's/: <.*//' -e 's/[0-9]*: //' -e 's/[ ]\+/ /g') $(date --rfc-3339=ns)
}

function send {
    TOPIC=$1
    BODY=$2
    rabbitmqadmin.py --user=${RABBIT_USER} --pass=${RABBIT_PASS} -H rabbitmq publish exchange=amq.topic routing_key="$TOPIC" payload="$BODY"
    echo -e "$TOPIC\t===>\t$BODY"
}

### Concurrent topic sends
send "$TOPIC_PREFIX.monitor.uptime" "$(get_uptime)" &
send "$TOPIC_PREFIX.monitor.memory" "$(get_mem_usage)" &
send "$TOPIC_PREFIX.monitor.cpu" "$(get_cpu_usage)" &
send "$TOPIC_PREFIX.monitor.ifs" "$(get_if_stats)" &
send "$TOPIC_PREFIX.monitor.ios" "$(get_io_stats | sed 's/,\]/]/')" &

sleep 5 # TODO DELETE when send will be implemented with rabbitmqadmin tool (just for tests)
