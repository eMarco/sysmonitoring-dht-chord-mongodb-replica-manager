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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.concurrent.TimeUnit.SECONDS;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
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

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class NodeSessionBean extends BaseNode implements NodeSessionBeanLocal {

    private static final int PERIOD = 10; //seconds

    private BaseNode        successor, predecessor;
    private FingerTable     fingerTable;
    private Storage         storage;

    private Boolean         hasJoined = false;
    private NodeReference   joinEntryPoint = new NodeReference("distsystems_replicamanager_1");

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Resource
    private SessionContext context;

    public NodeSessionBean() {
        //init();
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

        scheduler.schedule(() -> {
            System.out.println(this.nodeRef.getHostname() + " JOINING THE RING");
            if (!isLocal(joinEntryPoint)) {
                while (!this.join(joinEntryPoint)) {

                    System.out.println("JOIN FAILED");
                    try {
                        System.out.println("SLEEPING FAILED");
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NodeSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("JOIN SUCCESSFUL");
            } else {
                this.hasJoined = true;
            }
        }, 10, TimeUnit.SECONDS);

        TimerService timerService = context.getTimerService();
        timerService.getTimers().forEach((Timer t) -> t.cancel());
        Timer t  =  timerService.createIntervalTimer(15000, PERIOD*1000,  new TimerConfig(new String("STABILIZE"), true));
        Timer t2 =  timerService.createIntervalTimer(16000, 2*PERIOD*1000,  new TimerConfig(new String("FIXFINGERS"), true));
    }

    @Timeout
    public void timeout(Timer timer) {
        System.err.println("TIMERBEAN: timeout occurred " + timer.getInfo()); 
        if (timer.getInfo().equals("STABILIZE")) {
            System.err.println("STABILIZE CALLING"); 
            stabilize();
        }
        if (timer.getInfo().equals("FIXFINGERS")) {
            System.err.println("FIXFINGERS CALLING"); 
            fixFingers();
        }
    }

    /************** RING INIT/CONSISTENCY METHODS *****************/

    /***
     * Create new Chord Ring
     */
    private void create() {
        this.predecessor    = null;
        this.successor      = getReference(this.nodeRef);
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

        this.successor = this.getReference(newSuccessor);
        System.out.println("NEW SUCCESSOR " + this.successor.getNodeReference());

        NodeReference successorsPredecessor = this.successor.notify(this.nodeRef);

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
                    && (
                        // the notifying node's ID is greater than our predecessor's. In this case, we're "enlarging" the ring
                        this.predecessor.getNodeReference().compareTo(nodeRef) <= 0
                        || // OR
                        // the notifying node's ID is lower than ours. In this case, we're "enlarging" the ring
                        this.nodeRef.compareTo(nodeRef) >= 0
                    )
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

    /***
     * Stabilize the ring.
     * Called periodically, asks the successor about its predecessor, verifies if our immediate
     * successor is consistent, and tells the successor about us.
     *
     * Schedule this method every PERIOD
     */
    //@Schedule(second = "*/" + PERIOD, minute = "*", hour = "*", persistent = false)
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
                            this.successor.getNodeReference().compareTo(this.nodeRef) < 0
                            // AND
                            && (
                                // the node ID of the successor's predecessor is lower than our successor's. In this case, we're "enlarging" the ring
                                this.successor.getNodeReference().compareTo(successorsPredecessor) >= 0
                                || // OR
                                this.nodeRef.compareTo(successorsPredecessor) <= 0
                            )
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
        List<GenericValue> myKeys = this.successor.getLessThanAndRemove(this.nodeRef.getNodeId());
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
     *
     * Schedule this method every PERIOD
     */
    //@Schedule(second = "*/" + 2*PERIOD, minute = "*", hour = "*", persistent = false)
    private void fixFingers() {
        if (!hasJoined) return;

        // System.out.println("FIXING FINGERS");

        List<NodeReference> tableEntries = new LinkedList<>();

        // Add this node...
        tableEntries.add(this.nodeRef);

        // ... and both its successor and predecesor node
        if (this.successor != null) tableEntries.add(this.successor.getNodeReference());
        if (this.predecessor != null) tableEntries.add(this.predecessor.getNodeReference());

        for (int i = 0; i < Key.LENGHT; i++) {
            tableEntries.add(
                    this.findSuccessor(
                            this.nodeRef.getNodeId().sumPow(i)
                    )
            );
        }

        this.fingerTable.replace(tableEntries);
    }

    /******** DHT/FUNCTIONAL/STORAGE METHODS ********/

    /***
     *
     * @param key
     * @return
     */
    @Override
    public List<GenericValue> get(Key key) {
        System.out.println("SEARCHING DB FOR KEY: " + key);
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
            if (this.successor == null || isLocal(this.successor)) {
//                System.out.println("GETTING THE FIRST FROM THE FINGERTABLE");
                return fingerTable.getFirst();
            }

            return this.successor.getNodeReference();
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
        if (obj == null || this.predecessor == null) {
            return false;
        }

        return this.predecessor.getNodeReference().equals(obj);
    }

    /***
     *
     * @return
     */
    public boolean checkPredecessor() {
        return this.predecessor.getNodeReference()
                .equals(new RemoteNodeProxy(this.predecessor
                        .getNodeReference()).ping());
    }

    /***
     *
     * @return
     */
    @Override
    public NodeReference getPredecessor() {
        return (this.predecessor != null) ? this.predecessor.getNodeReference() : null;
    }

    /*** TESTING/DEVELOPMENT METHODS ****/

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
        //return findSuccessor(new NodeReference(thisRef.getNodeId(), "")).toString();

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
}
