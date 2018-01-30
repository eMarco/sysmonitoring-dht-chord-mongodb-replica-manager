# Container for the datamanager
FROM jboss/wildfly

# Require arguments
ARG WILDFLY_ADMIN=root
ARG WILDFLY_PASSWORD=toor

RUN /opt/jboss/wildfly/bin/add-user.sh $WILDFLY_ADMIN $WILDFLY_PASSWORD --silent

ARG USER
# Override jboss user in wildfly official image to avoid any trouble in r\w the mounted volume
USER $USER

# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
