# RabbitMQ image
FROM rabbitmq:3-management

RUN apt-get update && apt-get install -y host

# Make code dir
RUN mkdir /code
WORKDIR /code

COPY init.sh /usr/local/bin/init.sh
RUN chmod +x /usr/local/bin/init.sh

CMD /usr/local/bin/init.sh
