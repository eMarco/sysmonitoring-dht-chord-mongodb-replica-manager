# A basic container for the python message handler
FROM debian:stretch-slim

RUN apt-get update && apt-get install -y python3 python3-pip curl
RUN pip3 install pika

RUN pip3 install requests websockets ArgumentParser
RUN apt-get update && apt-get install -y stress

RUN mkdir /code
WORKDIR /code

CMD python3 -u main.py -u ${RABBIT_USER} -p ${RABBIT_PASS}
