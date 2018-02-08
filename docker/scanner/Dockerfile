FROM distsystems_msg_handler

# Dependencies for scanner
RUN apt-get install -y procps iproute sysstat
RUN apt-get update && apt-get install -y stress

# Dependencies for Replica identification
RUN apt-get install -y host

# Pushing crontab to the root user (this is a workaround: global crontab is not working well inside the container)
COPY crontab /root/crontab
RUN crontab -u root /root/crontab

CMD ./stress.sh

