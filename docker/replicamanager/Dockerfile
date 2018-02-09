# Container for the replicamanager
FROM distsystems_datamanager

# MongoDB
# Overriding user jboss, rollback to root
USER root
ADD ./replicamanager/mongodb-org.repo /etc/yum.repos.d/mongodb-org.repo
RUN yum install -y mongodb-org
RUN mkdir -p /data/db

# run.sh executes mongod and the application server
CMD ["/usr/local/bin/run.sh"]

