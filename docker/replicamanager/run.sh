#!/bin/bash
mongod &
ln -s /code/*.ear /opt/jboss/wildfly/standalone/deployments/$(basename $(ls /code/*.ear))

sudo -u jboss /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

