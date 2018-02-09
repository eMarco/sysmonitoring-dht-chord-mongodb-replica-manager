# Container for the datamanager
FROM jboss/wildfly

# Require arguments
ARG WILDFLY_ADMIN=root
ARG WILDFLY_PASSWORD=toor

RUN /opt/jboss/wildfly/bin/add-user.sh $WILDFLY_ADMIN $WILDFLY_PASSWORD --silent

USER root
RUN yum install -y sudo
ADD ./replicamanager/run.sh /usr/local/bin/run.sh
RUN chmod +x /usr/local/bin/run.sh

# run.sh executes the application server
CMD ["/usr/local/bin/run.sh"]

