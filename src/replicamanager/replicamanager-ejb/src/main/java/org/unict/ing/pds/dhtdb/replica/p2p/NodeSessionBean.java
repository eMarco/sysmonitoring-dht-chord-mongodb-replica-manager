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
import static javax.ejb.LockType.READ;
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
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class NodeSessionBean extends BaseNode implements NodeSessionBeanLocal {

    private static final int PERIOD = 30; //seconds

    private BaseNode        successor, predecessor;
    private FingerTable     fingerTable;
    private Storage         storage;

    private Boolean         hasJoined = false;

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

        System.out.println(this.nodeRef.getHostname() + " JOINING THE RING");
        if (!this.nodeRef.getHostname().equals("distsystems_replicamanager_1")) {

            while (!this.join(new NodeReference("distsystems_replicamanager_1"))) {
                System.out.println("JOIN FAILED");
            }
            System.out.println("JOIN SUCCESSFUL");
        }
        else this.hasJoined = true;
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
        CPUStat x = new CPUStat((float)0.5, 4, "asd", myKey.toString(), "CPUStat");
        CPUStat y = new CPUStat((float)0.8, 4, "asd", myKey2.toString(), "CPUStat");
        write(myKey, x);
        //write(myKey2, y);
        ret += new Gson().toJson(lookup(myKey));

        return ret;
    }


    /***
     * Stabilize the ring.
     * Called periodically, asks the successor about its predecessor, verifies if our immediate
     * successor is consistent, and tells the successor about us.
     *
     * Schedule this method every PERIOD
     */
    @Schedule(second = "*/" + PERIOD, minute = "*", hour = "*", persistent = false)
    private void stabilize() {
        if (!hasJoined) return;

        System.out.println(this.nodeRef.getHostname() + " STABILIZE TRIGGERED. SUCCESSOR NODE " + this.successor.getNodeReference());
        // TODO : Fix NPE

        NodeReference successorsPredecessor;
        if (isLocal(this.successor)) {
            if (this.predecessor != null)
                successorsPredecessor = this.predecessor.getNodeReference();
            else {
                System.out.println("SUCCESSORS PREDECESSOR IS NULL");
                return;
            }
        }
        else {
            successorsPredecessor = this.successor.getPredecessor();
        }


        if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
            System.out.println(this.nodeRef.getHostname() + " SUCCESSORS PREDECESSOR : " + successorsPredecessor.getHostname() + " " + this.getNodeReference().compareTo(successorsPredecessor) + " " + successorsPredecessor.compareTo(this.successor.getNodeReference()));
            // if (this.successor == this
            if (isLocal(this.successor)
                    // OR if successorsPredecessor ∈ (this, successor))
                    || (this.getNodeReference().compareTo(successorsPredecessor) < 0 && successorsPredecessor.compareTo(this.successor.getNodeReference()) < 0)

                    || // OR if:
                        (
                            // our successor has a lower key than ours (we're the last in the ring)
                            this.successor.getNodeReference().compareTo(this.nodeRef) <= 0
                            // AND
                            &&
                            // the node ID of the successor's predecessor is lower than our successor's. In this case, we're "enlarging" the ring
                            this.successor.getNodeReference().compareTo(successorsPredecessor) > 0
                        )
                    ) {
                System.out.println(this.nodeRef.getHostname() + " UPDATED SUCCESSOR TO " + successorsPredecessor.getHostname());
                // Set the new successor and notify it about its new predecessor
                this.successor = getReference(successorsPredecessor);

                // Add successor to FingerTable
                fingerTable.addNode(successorsPredecessor);
            }
            else {
                System.out.println(this.nodeRef.getHostname() + " SUCCESSOR STILL " + this.successor.getNodeReference().getHostname());
            }

            if (!isLocal(this.successor)) {
                successorsPredecessor = this.successor.notify(this.nodeRef);

                if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
                    // This is not right. Set our new successor

                    // TODO check if legit?
                    // TODO: notify?
                    this.successor = getReference(successorsPredecessor);

                    fingerTable.addNode(successorsPredecessor);
                }
            }
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
        this.hasJoined = false;

        if (isLocal(_entryPoint)) {
            // Join requires an external node
            System.out.println("Trying to build a ring with... myself?");
            return false;
        }

        BaseNode entryPoint = new RemoteNodeProxy(_entryPoint);

        NodeReference newSuccessor = entryPoint.findSuccessor(this.nodeRef.getNodeId());
        if (newSuccessor == null) {
            return false;
        }

        this.successor = this.getReference(newSuccessor);
        System.out.println("NEW SUCCESSOR " + this.successor.getNodeReference());

        NodeReference successorsPredecessor = this.successor.notify(this.nodeRef);

        if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
            // ERROR
            System.out.println("Error joining the ring. Successor didn't set this node as predecessor.");

            fingerTable.addNode(successorsPredecessor);
            return false;
        }

        this.fillFingertable();

        //this.moveKeys();
        this.hasJoined = true;

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
    @Schedule(second = "*/" + 2*PERIOD, minute = "*", hour = "*", persistent = false)
    private void fixFingers() {
        if (!hasJoined) return;

//        System.out.println("FIXING FINGERS");

        List<NodeReference> tableEntries = new LinkedList<>();

        // Add this node
        tableEntries.add(this.nodeRef);

        for (int i = 0; i < Key.LENGHT; i++) {
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
        NodeReference nodeRef = fingerTable.getClosestPrecedingNode(key);

        if (isPredecessor(nodeRef)) {
//            System.out.println("The closestPrecedingNode is my predecessor; I'm the owner for the key " + key);
            return this.nodeRef;
        }

        if (isLocal(nodeRef)) {
            // return (successor)
            System.out.println("I am the the closestPrecedingNode; My successor the owner for the key " + key);
            if (this.successor == null || isLocal(this.successor)) {
                System.out.println("GETTING THE FIRST FROM THE FINGERTABLE");
                return fingerTable.getFirst();
            }

            return this.successor.getNodeReference();
        }

        // get the closest preceding node and trigger the findSuccessor (remote)
//        System.out.println("Looking for a candidate remote node as successor for the given key (" + key +") : " + nodeRef);

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
        System.out.println(this.nodeRef.getHostname() + " NODE " + nodeRef.getHostname() + " wants to become our predecessor");
        // Check if predecessor is null
        if (this.predecessor == null
                ||
                // OR if the notifying node's ID: predecessor.ID < NN.ID < this.ID
                (this.predecessor.getNodeReference().compareTo(nodeRef) < 0 && nodeRef.compareTo(this.nodeRef) < 0)
                ||
                // OR if:
                (
                    // our predecessor has a greater key than ours (we're the first in the ring)
                    this.predecessor.getNodeReference().compareTo(this.nodeRef) > 0
                    // AND
                    &&
                    // the notifying node's ID is greater than our predecessor's. In this case, we're "enlarging" the ring
                    this.predecessor.getNodeReference().compareTo(nodeRef) <= 0
                )
            ) {
            System.out.println(this.nodeRef.getHostname() + " NOTIFY SUCCESSFULL");
            this.predecessor = getReference(nodeRef);

            // Add predecessor to FingerTable
            fingerTable.addNode(nodeRef);
            return nodeRef;
        }

        System.out.println(this.nodeRef.getHostname() + " NOTIFY FAILED: " + this.predecessor.getNodeReference().compareTo(nodeRef) + " " + nodeRef.compareTo(this.nodeRef) + " " + this.predecessor.getNodeReference().compareTo(this.nodeRef) + " " + this.predecessor.getNodeReference().compareTo(nodeRef));

        // Return our predecessor
        return this.predecessor.getNodeReference();
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
