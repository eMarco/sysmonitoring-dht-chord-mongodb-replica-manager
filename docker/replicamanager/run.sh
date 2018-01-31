#!/bin/bash
mongod &
for i in $(ls /code/target/*.ear); do
    ln -s $i /opt/jboss/wildfly/standalone/deployments/$(basename $i)
done
sudo -u jboss /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0

