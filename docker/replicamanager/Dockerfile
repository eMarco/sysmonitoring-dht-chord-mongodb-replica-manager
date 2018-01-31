# Container for the datamanager
FROM jboss/wildfly

# MongoDB
# Overriding user jboss, rollback to root
USER root
ADD ./replicamanager/mongodb-org.repo /etc/yum.repos.d/mongodb-org.repo
RUN yum install -y mongodb-org sudo
RUN mkdir -p /data/db

# Require arguments
ARG WILDFLY_ADMIN=root
ARG WILDFLY_PASSWORD=toor

USER jboss
RUN /opt/jboss/wildfly/bin/add-user.sh $WILDFLY_ADMIN $WILDFLY_PASSWORD --silent

USER root
ADD ./replicamanager/run.sh /usr/local/bin/run.sh
RUN chmod +x /usr/local/bin/run.sh

# run.sh executes mongod and the application server
CMD ["/usr/local/bin/run.sh"]

