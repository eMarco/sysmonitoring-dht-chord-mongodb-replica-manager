# A basic container for the python message handler
FROM debian:stretch-slim

# Install Python3 and stress from repos
RUN apt-get update && apt-get install -y python3 python3-pip curl host
RUN pip3 install pika

# Symbolic linking python3 to python
RUN ln -s /usr/bin/python3 /usr/bin/python

RUN pip3 install requests websockets ArgumentParser pika

RUN mkdir /code
WORKDIR /code

CMD python3 -u main.py -u ${RABBIT_USER} -p ${RABBIT_PASS}
