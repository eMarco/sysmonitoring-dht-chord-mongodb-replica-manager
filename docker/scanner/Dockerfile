# Simple Debian Docker container for the scanners
FROM debian:stable-slim

# Install Python3 and stress from repos
RUN apt-get update && apt-get install -y python3 python3-pip curl stress
RUN pip3 install pika


# Symbolic linking python3 to python
RUN ln -s /usr/bin/python3 /usr/bin/python

# Dependencies for scanner
RUN apt-get install -y procps iproute sysstat

# Dependencies for Replica identification
RUN apt-get install -y host
# Bash completion
# RUN sh -c 'rabbitmqadmin --bash-completion > /etc/bash_completion.d/rabbitmqadmin'

COPY crontab /root/crontab
RUN crontab -u root /root/crontab

# Make code dir
RUN mkdir /code
WORKDIR /code

ENTRYPOINT ["bash", "./stress.sh"]

