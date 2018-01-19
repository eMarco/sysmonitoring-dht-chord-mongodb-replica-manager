# A basic container for testing with all the minimal tools for the project
FROM debian:stretch-slim

RUN apt-get update
RUN apt-get install -y python3 python3-pip curl
RUN pip3 install pika requests websockets

RUN apt-get install -y vim git openssh-server
RUN mkdir /code
WORKDIR /code

# TODO default ssh user
RUN mkdir /run/sshd
EXPOSE 22
CMD ["/usr/sbin/sshd", "-D", "-e"]

