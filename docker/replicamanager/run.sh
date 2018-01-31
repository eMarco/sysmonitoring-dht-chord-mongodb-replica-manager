#!/bin/bash
mongod &
sudo -u jboss ln -s /code/target/*.ear /opt/jboss/wildfly/standalone/deployments/$(basename $(ls /code/target/*.ear))

sudo -u jboss /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

