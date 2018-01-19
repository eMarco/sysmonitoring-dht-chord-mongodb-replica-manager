#!/usr/bin/env python3
# Just a script to test topic-based sending

import pika
import sys
import os

user  = os.environ.get('RABBIT_USER')
passw = os.environ.get('RABBIT_PASS')

credentials = pika.PlainCredentials(user, passw)
connection  = pika.BlockingConnection(pika.ConnectionParameters(host='rabbitmq', credentials=credentials))
channel     = connection.channel()

exchange    = 'amq.topic'
topic       = 'myTopic'

channel.exchange_declare(exchange=exchange, exchange_type='topic', durable=True)
channel.queue_declare(queue=topic, durable=True, auto_delete=False)
channel.queue_bind(queue=topic, exchange=exchange, routing_key=("monitor.%s.*" % (topic)))
channel.basic_publish(exchange=exchange, routing_key="monitor.%s.myHostX" % (topic), body="Hello")

print("[x] Created and tested %r" % (topic))

connection.close()

