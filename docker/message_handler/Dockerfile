# A basic container for the python message handler
FROM debian:stretch-slim

RUN apt-get update
RUN apt-get install -y python3 python3-pip curl
RUN pip3 install pika requests websockets

RUN mkdir /code
WORKDIR /code

ENTRYPOINT ["python3", "-u", "main.py"]
