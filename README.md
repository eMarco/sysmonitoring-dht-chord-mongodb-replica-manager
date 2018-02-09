## An implementation of a Cloud monitor using LIGHT (Lightweight Index for complex queries over DHTs) for a distributed MongoDB on Chord overlay network

### Universita' degli studi di Catania

#### LM Ingegneria informatica 

##### Alessandro Di Stefano - Marco Grassia

**Read the PDF (currently italian, leggimi.pdf) for more infos.**

This is a basic implementation of Chord to manage a MongoDB database with a single collection, providing indexing based on timestamps for the scenario of a Cloud Monitoring System for Virtual Nodes.

The scenario was "virtualized" for the demo through docker-compose. The datamanager is responsible of receiving statistics about the virtual machines (scanner) by the Message Handler: it put the statistics in the distributed MongoDB under Chord indexing with LIGHT by timestamp.

#### References

- Distributed Segment Tree: Support of Range query and cover  query over DHT (Zheng et al.) [https://tinyurl.com/yd8declm]
- m-LIGHT: A LIGhtweight multidimensional index for Complex Queries over DHTs (Tang et al.) [https://tinyurl.com/y7tdgcag]
- https://wiki.apache.org/cassandra/ArchitectureSSTable
- Layering a DBMS on a DHT-Based Storage Engine (Ribas et al.) [https://tinyurl.com/ybkscqzk]
- Chord: A scalable peer-to-peer lookup service for Internet applications (Stoica et al.) [https://tinyurl.com/y9uf5opr]
- LIGHT: A query-efficient yet low-maintenance indexing scheme over DHTs (Tang et al.) [https://tinyurl.com/ydaxultx]
- Prefix Hash Tree: An indexing Data Structure over Distributed Hash Tables (Ramabhadra et al.) [https://tinyurl.com/ybu3e922]
- An Adaptive Protocol for Efficient Support of Range queries in DHT-based Systems (Gao et al.) [https://tinyurl.com/yc6l6n7s]
