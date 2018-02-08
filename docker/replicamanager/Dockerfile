# Container for the replicamanager
FROM jboss/wildfly

# Require arguments
ARG WILDFLY_ADMIN=root
ARG WILDFLY_PASSWORD=toor

RUN /opt/jboss/wildfly/bin/add-user.sh $WILDFLY_ADMIN $WILDFLY_PASSWORD --silent

USER root
RUN yum install -y sudo
ADD ./replicamanager/run.sh /usr/local/bin/run.sh
RUN chmod +x /usr/local/bin/run.sh

# MongoDB
# Overriding user jboss, rollback to root
USER root
ADD ./replicamanager/mongodb-org.repo /etc/yum.repos.d/mongodb-org.repo
RUN yum install -y mongodb-org
RUN mkdir -p /data/db

# run.sh executes mongod and the application server
CMD ["/usr/local/bin/run.sh"]

