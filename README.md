## An implementation of a Cloud monitor using LIGHT (Lightweight Index for complex queries over DHTs) for a distributed MongoDB on Chord overlay network

## Università degli Studi di Catania - LM Ingegneria informatica

### Corso di sistemi distribuiti

#### Alessandro Di Stefano - Marco Grassia

**[Read the .pdf](leggimi.pdf) (leggimi.pdf, currently italian only) for more infos.**

**[The Java Doc and some UML is available here.](https://emarco.github.io/sysmonitoring-dht-chord-mongodb-replica-manager/)**

![Screenshot](docs/img.jpg?raw=true)
![Screenshot](docs/img2.jpeg?raw=true)

Synthetic class diagram available in uml/: [svg](docs/class-diagram.svg) [jpg](docs/class-diagram.jpg)

This is a basic implementation of Chord used to manage a MongoDB database with a single collection.\
Also, it provides indexing based on timestamps for the scenario of a Cloud Monitoring System for Virtual Nodes.

We simulated a scenario consisting of virtual machines in a data center. In short:\
The VMs are monitored by "scanners", which send the collected data to a RabbitMQ queue.\
A component, called Message Handler, receives the readings from the shared queue and forwards them to the Datamanager, which is a client for the distributed DB.

For the demo, the scenario was run in Docker through docker-compose.

### How to run the demo:
1. Clone this repository
1. Build the Maven project `allMaven`
1. In a shell:

```   
   $ cd docker/
   $ docker-compose up
```

You need to have the rights to bind port 80, and look at your SELinux policies if you use it.

![Screenshot](docs/img3.jpg?raw=true)

### References

- Distributed Segment Tree: Support of Range query and cover  query over DHT (Zheng et al.) [https://tinyurl.com/yd8declm]
- m-LIGHT: A LIGhtweight multidimensional index for Complex Queries over DHTs (Tang et al.) [https://tinyurl.com/y7tdgcag]
- https://wiki.apache.org/cassandra/ArchitectureSSTable
- Layering a DBMS on a DHT-Based Storage Engine (Ribas et al.) [https://tinyurl.com/ybkscqzk]
- Chord: A scalable peer-to-peer lookup service for Internet applications (Stoica et al.) [https://tinyurl.com/y9uf5opr]
- LIGHT: A query-efficient yet low-maintenance indexing scheme over DHTs (Tang et al.) [https://tinyurl.com/ydaxultx]
- Prefix Hash Tree: An indexing Data Structure over Distributed Hash Tables (Ramabhadra et al.) [https://tinyurl.com/ybu3e922]
- An Adaptive Protocol for Efficient Support of Range queries in DHT-based Systems (Gao et al.) [https://tinyurl.com/yc6l6n7s]
