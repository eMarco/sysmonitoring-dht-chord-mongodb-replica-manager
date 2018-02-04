/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import com.google.gson.Gson;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.unict.ing.pds.dhtdb.replica.storage.MongoDBStorage;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.replicamanager.BaseNode;
import org.unict.ing.pds.dhtdb.utils.replicamanager.FingerTable;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;
import org.unict.ing.pds.dhtdb.utils.replicamanager.NodeReference;
import org.unict.ing.pds.dhtdb.utils.replicamanager.RemoteNodeProxy;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class NodeSessionBean extends BaseNode implements NodeSessionBeanLocal {

    /***
     * CONFIG VARS
     */
    private static final int PERIOD = 5; //seconds
    private static final int JOIN_MULT      = 2;
    private static final int STABILIZE_MULT = 1;
    private static final int FIXFINGER_MULT = 10;

    /***
     * CONFIG VARS END
     */

    private BaseNode        successor, predecessor;
    private FingerTable     fingerTable;
    private Storage         storage;

    private Boolean         hasJoined = false;
    private NodeReference   joinEntryPoint = new NodeReference("distsystems_replicamanager_1");

    @Resource
    private SessionContext context;

    public NodeSessionBean() {
    }

    @PostConstruct
    private void init() {
        this.storage = new MongoDBStorage();

        // Starting chord
        this.nodeRef     = NodeReference.getLocal();
        this.fingerTable = new FingerTable();
        this.fingerTable.addNode(this.nodeRef);

        // Init the ring
        this.create();

        TimerService timerService = context.getTimerService();
        timerService.getTimers().forEach((Timer t) -> t.cancel());
        timerService.createIntervalTimer(2020, STABILIZE_MULT * PERIOD * 1000, new TimerConfig("STABILIZE", true));
        timerService.createIntervalTimer(4000, FIXFINGER_MULT * PERIOD * 1000, new TimerConfig("FIXFINGERS", true));
        if (!isLocal(joinEntryPoint)) {
            timerService.createIntervalTimer(1050,  JOIN_MULT * PERIOD * 1000, new TimerConfig("JOIN", true));
        } else {
            this.hasJoined = true;
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().equals("STABILIZE")) {
            System.err.println("STABILIZE CALLING");
            stabilize();
        }
        if (timer.getInfo().equals("FIXFINGERS")) {
            System.err.println("FIXFINGERS CALLING");
            fixFingers();
        }

        if (timer.getInfo().equals("JOIN")) {
            if (!this.join(joinEntryPoint)) {
                System.out.println(this.nodeRef.getHostname() + " JOIN FAILED");
            } else {
                System.out.println(this.nodeRef.getHostname() + " JOIN SUCCESSFUL");
                timer.cancel();
            }
        }
    }

    /************** RING INIT/CONSISTENCY METHODS *****************/

    /***
     * Create new Chord Ring
     */
    private void create() {
        this.setPredecessor(null);
        this.setSuccessor(getReference(this.nodeRef));
    }

    /***
     * Join a ring using an entry point
     * @param entryPoint
     * @return
     */
    private Boolean join(NodeReference entryPoint) {
        this.hasJoined = false;

        if (isLocal(entryPoint)) {
            // Join requires an external node
            System.out.println("Trying to build a ring with... myself?");
            return false;
        }

        NodeReference newSuccessor = new RemoteNodeProxy(entryPoint).findSuccessor(this.nodeRef.getNodeId());
        if (newSuccessor == null) {
            return false;
        }

        this.setSuccessor(this.getReference(newSuccessor));
        System.out.println("NEW SUCCESSOR " + this.getSuccessor().getNodeReference());

        NodeReference successorsPredecessor = this.getSuccessor().notify(this.nodeRef);

        if (successorsPredecessor == null) return false;
        else if(!isLocal(successorsPredecessor)) {
            // ERROR
            System.out.println("Error joining the ring. Successor didn't set this node as predecessor.");

            fingerTable.addNode(successorsPredecessor);

            // Retry joining using the new successorsPredecessor
            if (!entryPoint.equals(successorsPredecessor)) {
                this.joinEntryPoint = !isLocal(successorsPredecessor) ? successorsPredecessor : fingerTable.getLast();
//                this.joinEntryPoint = successorsPredecessor;
            }
            else {
                this.joinEntryPoint = !isLocal(fingerTable.getLast()) ? fingerTable.getLast() : fingerTable.getFirst();
            }

            return false;
        }

        this.fillFingertable();

        //this.moveKeys();
        this.hasJoined = true;

        return true;
    }

    @Override
    public NodeReference notify(NodeReference nodeRef) {
        System.out.println(this.nodeRef.getHostname() + " NODE " + nodeRef.getHostname() + " wants to become our predecessor");
        // Check if predecessor is null
        if (this.getPredecessor() == null
                ||
                // OR if the notifying node's ID: predecessor.ID < NN.ID < this.ID
                (this.getPredecessor().getNodeReference().compareTo(nodeRef) < 0 && nodeRef.compareTo(this.nodeRef) < 0)
                ||
                // OR if:
                (
                this.getPredecessor().getNodeReference().compareTo(this.nodeRef) > 0
                    // AND
                    && (
                this.getPredecessor().getNodeReference().compareTo(nodeRef) <= 0
                        || // OR
                        // the notifying node's ID is lower than ours. In this case, we're "enlarging" the ring
                        this.nodeRef.compareTo(nodeRef) >= 0
                    )
                )
            ) {
            System.out.println(this.nodeRef.getHostname() + " NOTIFY SUCCESSFULL");
            this.setPredecessor(getReference(nodeRef));

            // Add predecessor to FingerTable
            fingerTable.addNode(nodeRef);
            return nodeRef;
        }

        System.out.println(this.nodeRef.getHostname() + " NOTIFY FAILED: " + this.getPredecessor().getNodeReference().compareTo(nodeRef) + " " + nodeRef.compareTo(this.nodeRef) + " " + this.getPredecessor().getNodeReference().compareTo(this.nodeRef) + " " + this.getPredecessor().getNodeReference().compareTo(nodeRef));

        // Return our predecessor
        return this.getPredecessor().getNodeReference();
    }

    /***
     * Stabilize the ring.
     * Called periodically, asks the successor about its predecessor, verifies if our immediate
     * successor is consistent, and tells the successor about us.
     */
    private void stabilize() {
        if (!hasJoined) return;

        System.out.println(this.nodeRef.getHostname() + " STABILIZE TRIGGERED. SUCCESSOR NODE " + this.getSuccessor().getNodeReference());
        // TODO : Fix NPE

        NodeReference successorsPredecessor;
        if (isLocal(this.getSuccessor())) {
            if (this.getPredecessor() != null)
                successorsPredecessor = this.getPredecessor().getNodeReference();
            else {
                System.out.println("SUCCESSORS PREDECESSOR IS NULL");
                return;
            }
        }
        else {
            successorsPredecessor = this.getSuccessor().getPredecessorNodeRef();
        }


        if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
            System.out.println(this.nodeRef.getHostname() + " SUCCESSORS PREDECESSOR : " + successorsPredecessor.getHostname() + " " + this.getNodeReference().compareTo(successorsPredecessor) + " " + successorsPredecessor.compareTo(this.getSuccessor().getNodeReference()));
            // if (this.successor == this
            if (isLocal(this.getSuccessor())
                    // OR if successorsPredecessor ∈ (this, successor))
                    || (this.getNodeReference().compareTo(successorsPredecessor) < 0 && successorsPredecessor.compareTo(this.getSuccessor().getNodeReference()) < 0)

                    || // OR if:
                        (
                    this.getSuccessor().getNodeReference().compareTo(this.nodeRef) < 0
                            // AND
                            && (
                    this.getSuccessor().getNodeReference().compareTo(successorsPredecessor) >= 0
                                || // OR
                                this.nodeRef.compareTo(successorsPredecessor) <= 0
                            )
                        )
                    ) {
                System.out.println(this.nodeRef.getHostname() + " UPDATED SUCCESSOR TO " + successorsPredecessor.getHostname());
                // Set the new successor and notify it about its new predecessor
                this.setSuccessor(getReference(successorsPredecessor));

                // Add successor to FingerTable
                fingerTable.addNode(successorsPredecessor);
            }
            else {
                System.out.println(this.nodeRef.getHostname() + " SUCCESSOR STILL " + this.getSuccessor().getNodeReference().getHostname());
            }

            if (!isLocal(this.successor)) {
                successorsPredecessor = this.getSuccessor().notify(this.nodeRef);

                if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
                    // This is not right. Set our new successor

                    // TODO check if legit?
                    // TODO: notify?
                    this.setSuccessor(getReference(successorsPredecessor));

                    fingerTable.addNode(successorsPredecessor);
                }
            }
        }
        else System.out.println("SUCCESSORS PREDECESSOR LOCAL OR NULL");

        if (this.getPredecessor() != null) System.out.println(this.nodeRef.getHostname() + " CURRENT PREDECESSOR: " + this.getPredecessor().getNodeReference().getHostname());
        if (this.getSuccessor() != null) System.out.println(this.nodeRef.getHostname() + " CURRENT SUCCESSOR " + this.getSuccessor().getNodeReference().getHostname());

        System.out.println("STABILIZE ENDED");
    }

    /***
     * TODO
     * @param nodeRef
     */
    @Override
    public void bootstrap(NodeReference nodeRef) {
        //nodeRef.bootstrap(node);
    }

    /***
     *
     */
    private void moveKeys() {
        List<GenericValue> myKeys = this.getSuccessor().getLessThanAndRemove(this.nodeRef.getNodeId());
        put(myKeys);
    }

    /***
     *
     * @param key
     * @return
     */
    @Override
    public List<GenericValue> getLessThanAndRemove(Key key) {
        return storage.lessThanAndRemove(key);
    }

    /******* FINGERTABLE METHODS ********/

    /***
     *
     */
    private void fillFingertable() {
        this.fixFingers();
    }

    /***
     * Fix fingers.
     */
    private void fixFingers() {
        if (!hasJoined) return;

        // System.out.println("FIXING FINGERS");

        FingerTable newFingerTable = new FingerTable();

        // Add this node...
        newFingerTable.addNode(this.nodeRef);

        // ... and both its successor and predecesor node
        if (this.getSuccessor() != null) newFingerTable.addNode(this.getSuccessor().getNodeReference());
        if (this.getPredecessor() != null) newFingerTable.addNode(this.getPredecessor().getNodeReference());

        for (int i = 0; i < Key.LENGHT; i++) {
            newFingerTable.addNode(
                    this.findSuccessor(
                            this.nodeRef.getNodeId().sumPow(i)
                    )
            );
        }

        this.replaceFingers(newFingerTable);
    }

    @Lock(LockType.WRITE)
    private void replaceFingers(FingerTable newFingerTable) {
        this.fingerTable = newFingerTable;
    }

    /******** DHT/FUNCTIONAL/STORAGE METHODS ********/

    /***
     *
     * @param key
     * @return
     */
    @Override
    public List<GenericValue> get(Key key) {
        System.out.println("SEARCHING DB FOR KEY: " + key + " ON NODE " + this.nodeRef);
        List<GenericValue> foundValues = this.storage.find(key);
        System.out.println("FOUND " + foundValues.toString());
        // The returned list has length 0 or more
        return foundValues;
    }

    /***
     *
     * @param elem
     * @return
     */
    @Override
    public Boolean put(GenericValue elem) {
        System.out.println("PUT FOR KEY " + elem.getKey() + "TRIGGERED ON NODE " + this.nodeRef);
        this.storage.insert(elem);
        return true;
    }

    /***
     *
     * @param elem
     * @return
     */
    @Override
    public Boolean put(List<GenericValue> elem) {
        this.storage.insertMany(elem);
        return true;
    }

    /***
     * Acting as a client (TODO move to the right class)
     * Check if this.nodeRef is responsible for the given k or forward until the
     * proper node is found to return the result
     * @param key
     * @return
     */
    @Override
    public List<GenericValue> lookup(Key key) {
        System.out.println("LOOKUP FOR " + key);

        return this.getReference(this.findSuccessor(key)).get(key);
    }

    /***
     * Acting as a client (TODO move to the right class)
     * Check if this.nodeRef is responsible for the given k or forward until the
     * proper node is found to return the result
     * @param key
     * @param elem
     * @return
     */
    @Override
    public Boolean write(Key key, GenericValue elem) {
        elem.setKey(key);
        System.out.println("Trying to write");
        return this.getReference(this.findSuccessor(key)).put(elem);
    }

    /******** CHORD METHODS ********/

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
        NodeReference closestPrecedingNode = fingerTable.getClosestPrecedingNode(key);

        if (isPredecessor(closestPrecedingNode)) {
            // System.out.println("The closestPrecedingNode is my predecessor; I'm the owner for the key " + key);
            return this.nodeRef;
        }

        if (isLocal(closestPrecedingNode)) {
            // return (successor)
//            System.out.println("I am the the closestPrecedingNode; My successor the owner for the key " + key);
            if (this.getSuccessor() == null || isLocal(this.getSuccessor())) {
//                System.out.println("GETTING THE FIRST FROM THE FINGERTABLE");
                return fingerTable.getFirst();
            }

            return this.getSuccessor().getNodeReference();
        }

        // get the closest preceding node and trigger the findSuccessor (remote)
        // System.out.println("Looking for a candidate remote node as successor for the given key (" + key +") : " + nodeRef);

        return getReference(closestPrecedingNode).findSuccessor(key); // As NodeReference returned
    }

    /***
     *
     * @param key
     * @return
     */
    @Override
    public NodeReference findPredecessor(Key key) {
        // Actually for join (TODO improve)

        NodeReference closestPrecedingNode;

        closestPrecedingNode = fingerTable.getClosestPrecedingNode(key);

        if (isLocal(closestPrecedingNode)) {
            // return me

            System.out.println("I am the owner of this key's interval");

            return this.nodeRef;
        }
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            System.out.println("Looking for a candidate remote node as successor for the given key (" + key +") : " + closestPrecedingNode);

            return new RemoteNodeProxy(closestPrecedingNode).findPredecessor(key);
        }
    }

    /****** COMODO METHODS ********/

    /***
     *
     * @param nodeReference
     * @return
     */
    private BaseNode getReference(NodeReference nodeReference) {
        if (isLocal(nodeReference)) {
            return this;
        }
        else {
            return new RemoteNodeProxy(nodeReference);
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
        if (obj == null || this.getPredecessor() == null) {
            return false;
        }

        return this.getPredecessor().getNodeReference().equals(obj);
    }

    /***
     *
     * @return
     */
    public boolean checkPredecessor() {
        return this.getPredecessor().getNodeReference()
                .equals(new RemoteNodeProxy(this.getPredecessor()
                        .getNodeReference()).ping());
    }

    /***
     *
     * @return
     */
    @Override
    public NodeReference getPredecessorNodeRef() {
        return (this.getPredecessor() != null) ? this.getPredecessor().getNodeReference() : null;
    }

    /*** TESTING/DEVELOPMENT METHODS ****/

    // triggered by http://localhost:8081/replicamanager-web/webresources/generic
    @Override
    public String myTest() {
        return "";
    }

    // triggered by http://localhost:8081/replicamanager-web/webresources/generic/test2
    @Override
    public String myTest2() {
        String ret = "";//String.valueOf(this.checkPredecessor());
        int id = 1;
        if (this.nodeRef.getHostname().contains("1"))
            id = 2;
        Key myKey = new Key(String.valueOf(new Random().nextInt()), true);
        Key myKey2= new Key(String.valueOf(new Random().nextInt()), true);
        CPUStat x = new CPUStat((float)0.5, 4, "asd", myKey);
        CPUStat y = new CPUStat((float)0.8, 4, "asd", myKey);
        write(myKey, x);
        //write(myKey2, y);
        ret += new Gson().toJson(lookup(myKey));

        return ret;
    }


    /*** GETTERS AND SETTERS ****/


    /**
     * @return the successor
     */
    public BaseNode getSuccessor() {
        return successor;
    }

    /**
     * @param successor the successor to set
     */
    @Lock(LockType.WRITE)
    public void setSuccessor(BaseNode successor) {
        this.successor = successor;
    }

    /**
     * @return the predecessor
     */
    public BaseNode getPredecessor() {
        return predecessor;
    }

    /**
     * @param predecessor the predecessor to set
     */
    @Lock(LockType.WRITE)
    public void setPredecessor(BaseNode predecessor) {
        this.predecessor = predecessor;
    }
}
