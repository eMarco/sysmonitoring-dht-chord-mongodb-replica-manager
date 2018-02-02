/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;
import org.unict.ing.pds.dhtdb.utils.replicamanager.NodeReference;
import org.unict.ing.pds.dhtdb.utils.replicamanager.RemoteNodeProxy;
import org.unict.ing.pds.dhtdb.utils.replicamanager.BaseNode;
import com.google.gson.Gson;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.unict.ing.pds.dhtdb.replica.storage.MongoDBStorage;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@Startup
public class NodeSessionBean extends BaseNode implements NodeSessionBeanLocal {

    private BaseNode        successor, predecessor;
    private FingerTable     fingerTable;
    private Storage         storage;

    public NodeSessionBean() {
        //init();
    }

    @PostConstruct
    private void init() {
        this.nodeRef     = NodeReference.getLocal();
        this.fingerTable = new FingerTable();
        this.fingerTable.addNode(this.nodeRef);
        this.storage = new MongoDBStorage();

        // Init the ring
        this.create();

        int idToAdd = 1;
        if (this.nodeRef.getHostname().equals("distsystems_replicamanager_1"))
            idToAdd = 2;

        String node2 = "distsystems_replicamanager_" + idToAdd;
        NodeReference theOtherNode = new NodeReference(node2);
        this.fingerTable.addNode(theOtherNode);
    }

    // triggered by http://localhost:8081/replicamanager-web/webresources/generic
    @Override
    public String myTest() {
        //this.init();
        //return this.thisRef.toString();
        CPUStat x = new CPUStat((float)0.5, 4, "asd");
        CPUStat y = new CPUStat((float)0.8, 4, "asd");
        //return new Gson().toJson(x);
        // Using this node's id as key, just for tests

        //put(nodeRef.getNodeId(), x);
        //System.out.println("DB: " + new Gson().toJson(get(nodeRef.getNodeId())));
        //return new Gson().toJson(get(nodeRef.getNodeId()));
        //RemoteNodeProxy thisRefRemote = new RemoteNodeProxy(nodeRef);

        //List<GenericStat> ret = thisRefRemote.get(nodeRef.getNodeId());
        //System.out.println("GOT " + ret.toString());

        //return new Gson().toJson(ret);
//        return findSuccessor(new NodeReference(thisRef.getNodeId(), "")).toString();

        //System.out.println("PUT DONE");
        /*System.out.println("PRINTING FINGERTABLE: ");
        this.fingerTable.getTable().forEach((t) -> {
            System.out.println("NODE " + t.getHostname() + " " + t.getNodeId());
        });*/

        System.out.println(this.nodeRef.getHostname() + " JOINING THE RING");
        if (this.join(new NodeReference("distsystems_replicamanager_2"))) {
            System.out.println("JOIN SUCCESSFUL");
        }
        else {
            System.out.println("JOIN FAILED");
        }

        Key myKey = new Key(x.toString());
        Key myKey2 = new Key(y.toString());
        //write(myKey, x);
        //write(myKey2, y);
        return new Gson().toJson(lookup(myKey));
    }

    @Override
    public String myTest2() {
        String ret = String.valueOf(this.checkPredecessor());
        CPUStat x = new CPUStat((float)0.5, 4, "asd");
        CPUStat y = new CPUStat((float)0.8, 4, "asd");
        Key myKey = new Key(x.toString());
        Key myKey2 = new Key(y.toString());
        //write(myKey, x);
        //write(myKey2, y);
        ret += new Gson().toJson(lookup(myKey));
        
        return ret;  
    }
    
    private NodeReference successor(Key k) {
        return findSuccessor(k);
    }
    private void stabilize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void fixFingers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /***
     * Create new Chord Ring
     */
    private void create() {
        this.predecessor    = null;
        this.successor      = getReference(this.nodeRef);
    }

    /***
     * Join a ring using an entry point
     * @param _entryPoint
     * @return
     */
    private Boolean join(NodeReference _entryPoint) {
        if (isLocal(_entryPoint)) {
            // Join requires an external node
            System.out.println("Trying to build a ring with... myself?");
            return false;
        }

        BaseNode entryPoint = new RemoteNodeProxy(_entryPoint);
        this.successor = this.getReference(entryPoint.findSuccessor(this.nodeRef.getNodeId()));

        System.out.println("NEW SUCCESSOR " + this.successor.getNodeReference());

        NodeReference successorsPredecessor = this.successor.notify(this.nodeRef);
        if (!this.nodeRef.equals(successorsPredecessor)) {
            // ERROR
            System.out.println("Error joining the ring. Successor didn't set this node as predecessor.");

            return false;
        }

        return true;
    }

    @Override
    public Boolean put(Key k, GenericValue elem) {
        this.storage.insert(elem, k.toString());
        return true;
    }

    @Override
    public List<GenericValue> get(Key k) {
        System.out.println("SEARCHING DB FOR KEY: " + k.toString());
        System.out.println("FOUND " + this.storage.find(k.toString()).toString());
        // The returned list has length 0 or more
        return this.storage.find(k.toString());
    }

    // Acting as a client (TODO move to the right class)
    // Check if this.nodeRef is responsible for the given k or forward until the
    // proper node is found to return the result
    @Override
    public List<GenericValue> lookup(Key k) {
        System.out.println("LOOKUP FOR " + k + "!!!!");
        
        return this.getReference(this.findSuccessor(k)).get(k);
    }

    // Acting as a client (TODO move to the right class)
    // Check if this.nodeRef is responsible for the given k or forward until the
    // proper node is found to return the result
    @Override
    public Boolean write(Key k, GenericValue elem) {
        System.out.println("Trying to write");
        return this.getReference(this.findSuccessor(k)).put(k, elem);
    }

    @Override
    public void bootstrap(NodeReference nodeRef) {
//        nodeRef.bootstrap(node);
    }

    /***
     *
     * @param key
     * @return
     */
    @Override
    public NodeReference findSuccessor(Key key) {
        // Each key, nodeRef.nodeId (TODO fix), is stored on the first node
        // whose identifier, nodeId, is equal to or follows nodeRef.nodeId
        // in the identifier space; (TODO no equal sign on second (successor) condition? Needed in only one replica scenario)

        NodeReference nodeRef;

        nodeRef = fingerTable.getClosestPrecedingNode(key);
        
        if (isLocal(nodeRef)) {
            // return me

            System.out.println("I am the owner of this key's interval");

            return this.nodeRef;
        }
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            System.out.println("Looking for a candidate remote node as successor for the given key (" + key +") : " + nodeRef);

            return new RemoteNodeProxy(nodeRef).findSuccessor(key);
        }
    }

    private BaseNode getReference(NodeReference nodeRef) {
        if (isLocal(nodeRef)) {
            return this;
        }
        else {
            return new RemoteNodeProxy(nodeRef);
        }
    }

    public boolean isLocal(NodeReference obj) {
        if (obj == null) {
            return false;
        }

        return this.nodeRef.equals(obj);
    }

    @Override
    public NodeReference notify(NodeReference nodeRef) {
        System.out.println("NODE " + nodeRef + "wants to join the ring");
        // Check if predecessor is null OR the joining node's ID: predecessor.ID < JN.ID < this.ID
        if (this.predecessor == null ||
                (this.predecessor.getNodeReference().compareTo(nodeRef) < 0 && nodeRef.compareTo(this.nodeRef) < 0)) {
            System.out.println("JOIN SUCCESSFULL");
            this.predecessor = getReference(nodeRef);
            return nodeRef;
        }

        System.out.println("JOIN FAILED: " + this.predecessor.getNodeReference().compareTo(nodeRef) + " " + nodeRef.compareTo(this.nodeRef));
        return null;
    }
    
    public boolean checkPredecessor() {
        return this.predecessor.getNodeReference()
                .equals(new RemoteNodeProxy(this.predecessor
                        .getNodeReference()).ping());
    }
}
