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
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
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
//@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class NodeSessionBean extends BaseNode implements NodeSessionBeanLocal {

    private static final int PERIOD = 30; //seconds

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

        System.out.println(this.nodeRef.getHostname() + " JOINING THE RING");
        if (this.nodeRef.getHostname().equals("distsystems_replicamanager_1")) {
            if (this.join(new NodeReference("distsystems_replicamanager_2"))) {
                System.out.println("JOIN SUCCESSFUL");
            }
            else {
                System.out.println("JOIN FAILED");
            }
        }
        //this.predecessor = new RemoteNodeProxy(new NodeReference("distsystems_replicamanager_" + idToAdd));
    }

    // triggered by http://localhost:8081/replicamanager-web/webresources/generic
    @Override
    public String myTest() {
        //this.init();
        //return this.thisRef.toString();

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


        /*Key myKey = new Key(String.valueOf(new Random().nextInt()));
        Key myKey2= new Key(String.valueOf(new Random().nextInt()));
        CPUStat x = new CPUStat((float)0.5, 4, "asd", myKey.toString());
        CPUStat y = new CPUStat((float)0.8, 4, "asd", myKey2.toString());*/
        //write(myKey, x);
        //write(myKey2, y);
        return "";
    }

    @Override
    public String myTest2() {
        String ret = "";//String.valueOf(this.checkPredecessor());
        int id = 1;
        if (this.nodeRef.getHostname().contains("1"))
            id = 2;
        Key myKey = new Key(String.valueOf(new Random().nextInt()));
        Key myKey2= new Key(String.valueOf(new Random().nextInt()));
        CPUStat x = new CPUStat((float)0.5, 4, "asd", myKey.toString());
        CPUStat y = new CPUStat((float)0.8, 4, "asd", myKey2.toString());
        //write(myKey, x);
        //write(myKey2, y);
        ret += new Gson().toJson(lookup(myKey));

        return ret;
    }

    private NodeReference successor(Key k) {
        return findSuccessor(k);
    }

    /***
     * Stabilize the ring.
     * Called periodically, asks the successor about its predecessor, verifies if our immediate
     * successor is consistent, and tells the successor about us.
     *
     * Schedule this method every PERIOD
     */
    @Schedule(second = "*/" + PERIOD, minute = "*", hour = "*")
    private void stabilize() {
        System.out.println("STABILIZE TRIGGERED " + this.successor.getNodeReference());
        NodeReference successorsPredecessor = (isLocal(this.successor)) ? this.predecessor.getNodeReference() : this.successor.getPredecessor();

        if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
            System.out.println("SUCCESSORS PREDECESSOR : " + successorsPredecessor.getHostname());
            // if (this.successor == this || successorsPredecessor ∈ (this, successor))
            if (isLocal(this.successor) || (this.getNodeReference().compareTo(successorsPredecessor) < 0 && successorsPredecessor.compareTo(this.successor.getNodeReference()) < 0)) {
                // Set the new successor and notify it about its new predecessor
                this.successor = getReference(successorsPredecessor);

                // Add successor to FingerTable
                fingerTable.addNode(successorsPredecessor);
            }

            if (!isLocal(this.successor))
                this.successor.notify(this.getNodeReference());
        }
        else System.out.println("SUCCESSORS PREDECESSOR LOCAL OR NULL");

        if (this.predecessor != null) System.out.println(this.nodeRef.getHostname() + " CURRENT PREDECESSOR: " + this.predecessor.getNodeReference().getHostname());
        if (this.successor != null) System.out.println(this.nodeRef.getHostname() + " CURRENT SUCCESSOR " + this.successor.getNodeReference().getHostname());
        
        System.out.println("STABILIZE ENDED");
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
        this.successor = this.getReference(entryPoint.findPredecessor(this.nodeRef.getNodeId()));

        System.out.println("NEW SUCCESSOR " + this.successor.getNodeReference());

        NodeReference successorsPredecessor = this.successor.notify(this.nodeRef);
        if (!this.nodeRef.equals(successorsPredecessor)) {
            // ERROR
            System.out.println("Error joining the ring. Successor didn't set this node as predecessor.");

            return false;
        }

        this.fillFingertable();

        //this.moveKeys();

        return true;
    }

    /***
     *
     */
    private void fillFingertable() {
        this.fixFingers();
    }

    /***
     * Fix fingers.
     *
     * Schedule this method every PERIOD
     */
    @Schedule(second = "*/" + 2*PERIOD, minute = "*", hour = "*")
    private void fixFingers() {
        List<NodeReference> tableEntries = new LinkedList<>();

        // Add this node
        tableEntries.add(this.nodeRef);

        for (int i = 0; i < Key.LENGHT; i++) {
            System.out.println("FIXING FINGER " + i);
            tableEntries.add(
                    this.findSuccessor(
                            this.nodeRef.getNodeId().sumPow(i)
                    )
            );
        }

        this.fingerTable.replace(tableEntries);
    }

    public void moveKeys() {
        List<GenericValue> myKeys = this.successor.getLessThanAndRemove(this.nodeRef.getNodeId());
        put(myKeys);
    }

    @Override
    public Boolean put(GenericValue elem) {
        this.storage.insert(elem);
        return true;
    }

    @Override
    public Boolean put(List<GenericValue> elem) {
        this.storage.insertMany(elem);
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
        elem.setKey(k.toString());
        System.out.println("Trying to write");
        return this.getReference(this.findSuccessor(k)).put(elem);
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
        // TODO FIX: THIS IS WRONG!
        NodeReference nodeRef;

        nodeRef = fingerTable.getClosestPrecedingNode(key);
        try {
            System.out.println("ME: " + this.nodeRef);

            System.out.println("MySuccessor: " + this.successor.getNodeReference());
            System.out.println("LOWER: " + nodeRef);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        
        if (isPredecessor(nodeRef)) {
            System.out.println("The closestPrecedingNode is my predecessor; I'm the owner for the key " + key);
            return this.nodeRef;
        }

        if (isLocal(nodeRef)) {
            // return (successor)
            System.out.println("I am the the closestPrecedingNode; My successor the owner for the key " + key);
            return this.successor.getNodeReference();
        }

        // get the closest preceding node and trigger the findSuccessor (remote)
        System.out.println("Looking for a candidate remote node as successor for the given key (" + key +") : " + nodeRef);

        return getReference(nodeRef).findSuccessor(key); // As NodeReference returned
    }

    @Override
    public NodeReference findPredecessor(Key key) {
        // Actually for join (TODO improve)

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

            return new RemoteNodeProxy(nodeRef).findPredecessor(key);
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

    public boolean isLocal(BaseNode obj) {
        if (obj == null) {
            return false;
        }

        return this.nodeRef.equals(obj.getNodeReference());
    }

    public boolean isLocal(NodeReference obj) {
        if (obj == null) {
            return false;
        }

        return this.nodeRef.equals(obj);
    }

    public boolean isPredecessor(NodeReference obj) {
        if (obj == null || this.predecessor == null) {
            return false;
        }

        return this.predecessor.getNodeReference().equals(obj);
    }

    @Override
    public NodeReference notify(NodeReference nodeRef) {
        System.out.println("NODE " + nodeRef + "wants to become our predecessor");
        // Check if predecessor is null OR the joining node's ID: predecessor.ID < JN.ID < this.ID
        if (this.predecessor == null ||
                (this.predecessor.getNodeReference().compareTo(nodeRef) < 0 && nodeRef.compareTo(this.nodeRef) < 0)) {
            System.out.println("NOTIFY SUCCESSFULL");
            this.predecessor = getReference(nodeRef);

            // Add predecessor to FingerTable
            fingerTable.addNode(nodeRef);
            return nodeRef;
        }

        System.out.println("NOTIFY FAILED: " + this.predecessor.getNodeReference().compareTo(nodeRef) + " " + nodeRef.compareTo(this.nodeRef));
        return null;
    }

    public boolean checkPredecessor() {
        return this.predecessor.getNodeReference()
                .equals(new RemoteNodeProxy(this.predecessor
                        .getNodeReference()).ping());
    }

    @Override
    public NodeReference getPredecessor() {
        return (this.predecessor != null) ? this.predecessor.getNodeReference() : null;
    }

    @Override
    public List<GenericValue> getLessThanAndRemove(Key key) {
        return storage.lessThanAndRemove(key.toString());
    }

}
