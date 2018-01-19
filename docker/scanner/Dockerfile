# Simple Debian Docker container for the scanners

FROM debian:stable-slim

# Install Python3 and stress from repos
RUN apt-get update && apt-get install -y python3 stress

# Download RabbitMQadmin script...
ADD https://raw.githubusercontent.com/rabbitmq/rabbitmq-management/v3.7.2/bin/rabbitmqadmin /usr/local/bin/rabbitmqadmin.py

# ... and make it executable
RUN chmod +x /usr/local/bin/rabbitmqadmin.py

# Bash completion
# RUN sh -c 'rabbitmqadmin --bash-completion > /etc/bash_completion.d/rabbitmqadmin'

# Make code dir
RUN mkdir /code
WORKDIR /code

ENTRYPOINT ["sh", "./scanner.sh"]
