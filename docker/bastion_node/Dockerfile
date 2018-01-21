# A basic container for testing with all the minimal tools for the project
FROM debian:stretch-slim
# TODO optimize

# From message_handler
RUN apt-get update && apt-get install -y python3 python3-pip curl
RUN pip3 install pika requests websockets

RUN apt-get update && apt-get install -y vim git openssh-server
RUN mkdir /code
WORKDIR /code

# TODO default ssh user
RUN mkdir /run/sshd
EXPOSE 22

# From Scanner
# Download RabbitMQadmin script...
ADD https://raw.githubusercontent.com/rabbitmq/rabbitmq-management/v3.7.2/bin/rabbitmqadmin /usr/local/bin/rabbitmqadmin.py

# ... and make it executable
RUN chmod +x /usr/local/bin/rabbitmqadmin.py

# Symbolic linking python3 to python
RUN ln -s /usr/bin/python3 /usr/bin/python

# Dependencies for scanner
RUN apt-get update && apt-get install -y procps iproute sysstat

# Bash completion
#RUN sh -c 'rabbitmqadmin.py --bash-completion > /etc/bash_completion.d/rabbitmqadmin'

CMD ["/usr/sbin/sshd", "-D", "-e"]

