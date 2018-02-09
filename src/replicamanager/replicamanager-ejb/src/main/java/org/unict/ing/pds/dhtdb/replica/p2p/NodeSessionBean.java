/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.List;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.unict.ing.pds.dhtdb.replica.storage.DBConnectionSingletonSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.chord.FingerSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.common.BaseNode;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.common.RemoteNodeProxy;
import org.unict.ing.pds.dhtdb.utils.chord.RingSessionBeanLocal;

/**
 * Responsible for the Chord primitives
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class NodeSessionBean extends BaseNode implements NodeSessionBeanLocal {
    /***
     * CONFIGS for the timers of periodically called methods
     */
    private static final int PERIOD = 5; //seconds
    private static final int JOIN_MULT      = 2;
    private static final int STABILIZE_MULT = 1;
    private static final int FIXFINGER_MULT = 10;

    /***
     * CONFIG VARS END
     */
    
    @EJB
    private DBConnectionSingletonSessionBeanLocal dBConnectionSingletonSessionBean;
    
    @EJB
    private FingerSessionBeanLocal  fingerSessionBean;

    @EJB
    private RingSessionBeanLocal    ringSessionBean;

    private Storage         storage;

    private NodeReference   joinEntryPoint = NodeReference.MASTER_NODE;


    @Resource
    private SessionContext context;

    public NodeSessionBean() {
    }

    @PostConstruct
    private void init() {
        this.storage = dBConnectionSingletonSessionBean.getStorage();
        
        // Starting chord
        this.nodeRef     = NodeReference.getLocal();

        fingerSessionBean.addNode(this.nodeRef);

        // Init the ring
        this.create();

        TimerService timerService = context.getTimerService();
        timerService.getTimers().forEach((Timer t) -> t.cancel());
        timerService.createIntervalTimer(2020, STABILIZE_MULT * PERIOD * 1000, new TimerConfig("STABILIZE", true));
        timerService.createIntervalTimer(4000, FIXFINGER_MULT * PERIOD * 1000, new TimerConfig("FIXFINGERS", true));
        if (!isLocal(joinEntryPoint)) {
            timerService.createIntervalTimer(1050,  JOIN_MULT * PERIOD * 1000, new TimerConfig("JOIN", true));
        } else {
            setHasJoined((Boolean) true);
        }
    }

    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().equals("STABILIZE")) {
            stabilize();
        }
        if (timer.getInfo().equals("FIXFINGERS")) {
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

    /**
     * Create new Chord Ring with only "myself"
     */
    private void create() {
        setPredecessor(null);
        setSuccessor(getReference(this.nodeRef));
    }

    /***
     * Join a ring using an entry point
     * @param entryPoint
     * @return
     */
    private Boolean join(NodeReference entryPoint) {
        setHasJoined((Boolean) false);

        if (isLocal(entryPoint)) {
            // Join requires an external node
            System.out.println("Trying to build a ring with... myself?");
            return false;
        }

        NodeReference newSuccessor = new RemoteNodeProxy(entryPoint).findSuccessor(this.nodeRef.getNodeId());
        if (newSuccessor == null) {
            return false;
        }

        setSuccessor(getReference(newSuccessor));
        System.out.println("NEW SUCCESSOR " + getSuccessor().getNodeReference());

        NodeReference successorsPredecessor = getSuccessor().notify(this.nodeRef);

        if (successorsPredecessor == null) return false;
        else if(!isLocal(successorsPredecessor)) {
            // ERROR
            System.out.println("Error joining the ring. Successor didn't set this node as predecessor.");

            fingerSessionBean.addNode(successorsPredecessor);

            // Retry joining using the new successorsPredecessor
            if (!entryPoint.equals(successorsPredecessor)) {
                this.joinEntryPoint = !isLocal(successorsPredecessor) ? successorsPredecessor : fingerSessionBean.getLast();
                // this.joinEntryPoint = successorsPredecessor;
            }
            else {
                this.joinEntryPoint = !isLocal(fingerSessionBean.getLast()) ? fingerSessionBean.getLast() : fingerSessionBean.getFirst();
            }

            return false;
        }

        this.fillFingertable();

        //this.moveKeys();
        setHasJoined((Boolean) true);

        return true;
    }

    /**
     * Notify primitive of the Chord Protocol
     * @param nodeRef
     * @return 
     */
    @Override
    public NodeReference notify(NodeReference nodeRef) {
        System.out.println(this.nodeRef.getHostname() + " NODE " + nodeRef.getHostname() + " wants to become our predecessor");
        // Check if predecessor is null
        if (getPredecessor() == null
                ||
                // OR if the notifying node's ID: predecessor.ID < NN.ID < this.ID
                (getPredecessor().getNodeReference().compareTo(nodeRef) < 0 && nodeRef.compareTo(this.nodeRef) < 0)
                ||
                // OR if:
                (
                getPredecessor().getNodeReference().compareTo(this.nodeRef) > 0
                    // AND
                    && (
                getPredecessor().getNodeReference().compareTo(nodeRef) <= 0
                        || // OR
                        // the notifying node's ID is lower than ours. In this case, we're "enlarging" the ring
                        this.nodeRef.compareTo(nodeRef) >= 0
                    )
                )
            ) {
            System.out.println(this.nodeRef.getHostname() + " NOTIFY SUCCESSFULL");
            setPredecessor(getReference(nodeRef));

            // Add predecessor to FingerTable
            fingerSessionBean.addNode(nodeRef);
            return nodeRef;
        }

        System.out.println(this.nodeRef.getHostname() + " NOTIFY FAILED: " + getPredecessor().getNodeReference().compareTo(nodeRef) + " " + nodeRef.compareTo(this.nodeRef) + " " + getPredecessor().getNodeReference().compareTo(this.nodeRef) + " " + getPredecessor().getNodeReference().compareTo(nodeRef));

        // Return our predecessor
        return getPredecessor().getNodeReference();
    }

    /***
     * Stabilize the ring.
     * Called periodically, asks the successor about its predecessor, verifies if our immediate
     * successor is consistent, and tells the successor about us.
     */
    private void stabilize() {
        if (!getHasJoined()) return;

        NodeReference successorsPredecessor;
        if (isLocal(getSuccessor())) {
            if (getPredecessor() != null)
                successorsPredecessor = getPredecessor().getNodeReference();
            else {
                return;
            }
        }
        else {
            successorsPredecessor = getSuccessor().getPredecessorNodeRef();
        }


        if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
            // if (this.successor == this
            if (isLocal(getSuccessor())
                    // OR if successorsPredecessor ∈ (this, successor))
                    || (this.getNodeReference().compareTo(successorsPredecessor) < 0 && successorsPredecessor.compareTo(getSuccessor().getNodeReference()) < 0)

                    || // OR if:
                        (
                    getSuccessor().getNodeReference().compareTo(this.nodeRef) < 0
                            // AND
                            && (
                    getSuccessor().getNodeReference().compareTo(successorsPredecessor) >= 0
                                || // OR
                                this.nodeRef.compareTo(successorsPredecessor) <= 0
                            )
                        )
                    ) {
                // Set the new successor and notify it about its new predecessor
                setSuccessor(getReference(successorsPredecessor));

                // Add successor to FingerTable
                fingerSessionBean.addNode(successorsPredecessor);
            }

            if (!isLocal(getSuccessor())) {
                successorsPredecessor = getSuccessor().notify(this.nodeRef);
                if (successorsPredecessor != null && !isLocal(successorsPredecessor)) {
                    // This is not right. Set our new successor
                    setSuccessor(getReference(successorsPredecessor));
                    fingerSessionBean.addNode(successorsPredecessor);
                }
            }
        }
        
        /*System.out.println(this.nodeRef.getHostname() + " CURRENT PREDECESSOR: " + getPredecessor() == null ?
                "NULL" : getPredecessor().getNodeReference().getHostname());
        
        System.out.println(this.nodeRef.getHostname() + " CURRENT SUCCESSOR " + getSuccessor() == null ? 
                "NULL" : getSuccessor().getNodeReference().getHostname());
        */
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
        List<GenericValue> myKeys = getSuccessor().getLessThanAndRemove(this.nodeRef.getNodeId());
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
     * Fill the fingerTable
     */
    private void fillFingertable() {
        this.fixFingers();
    }

    /***
     * Fix fingerTable (recreates and Swap with LockWrite).
     */
    private void fixFingers() {
        if (!getHasJoined()) return;

        TreeSet<NodeReference> newFingerTable = new TreeSet<>();

        // Add this node...
        newFingerTable.add(this.nodeRef);

        // ... and both its successor and predecesor node
        if (getSuccessor() != null) newFingerTable.add(getSuccessor().getNodeReference());
        if (getPredecessor() != null) newFingerTable.add(getPredecessor().getNodeReference());

        for (int i = 0; i < Key.LENGTH; i++) {
            newFingerTable.add(
                    this.findSuccessor(
                            this.nodeRef.getNodeId().sumPow(i)
                    )
            );
        }

        fingerSessionBean.swapTable(newFingerTable);
    }


    /******** DHT/FUNCTIONAL/STORAGE METHODS ********/

    /***
     * Get a List of Values stored in the storage of the node executing this method with the given Key
     * @param key
     * @return 
     */
    @Override
    public List<GenericValue> get(Key key) {
        List<GenericValue> foundValues = this.storage.find(key);       
        // The returned list has length 0 or more
        return foundValues;
    }

    /***
     * Store a value in the storage of the node executing this method
     * @param elem
     * @return
     */
    @Override
    public Boolean put(GenericValue elem) {
        this.storage.insert(elem);
        return true;
    }

    /***
     * Sore a list of GenericValue in the storage of the node executing this method
     * @param elems
     * @return
     */
    @Override
    public Boolean put(List<GenericValue> elems) {
        this.storage.insertMany(elems);
        return true;
    }

    /**
     * Remove from the storage of the node executing this method
     * @param key
     * @return 
     */
    @Override
    public List<GenericValue> delete(Key key) {
        List<GenericValue> elems = this.get(key);
        this.storage.remove(key);
        return elems;
    }
    /***
     * Get a NodeReference to the successor of the given Key and make a Get in the associated peer
     * @param key
     * @return
     */
    @Override
    public List<GenericValue> lookup(Key key) {
        return getReference(this.findSuccessor(key)).get(key);
    }

    /***
     * Get a NodeReference to the successor of the given Key and make a put in the associated peer
     * @param key
     * @param elem
     * @return
     */
    @Override
    public Boolean write(Key key, GenericValue elem) {
        elem.setKey(key);
        System.out.println("Trying to write");
        return getReference(this.findSuccessor(key)).put(elem);
    }
    
    /**
     * Remove a Key-Value from the successor associated with the given Key
     * @param key
     * @return 
     */
    @Override
    public List<GenericValue> remove(Key key) {
        return getReference(this.findSuccessor(key)).delete(key);
    }

    /**
     * Update a Key-Value record from the successor associated with the given key
     * 
     * @param key
     * @param elems
     * @return 
     */
    @Override
    public Boolean update(Key key, List<GenericValue> elems) {
        BaseNode proxy = getReference(this.findSuccessor(key));
        proxy.delete(key);
        proxy.put(elems);
        return true;
    }
    /******** CHORD METHODS ********/

    /***
     * findSuccessor primitive of chord protocol
     * 
     * @param key
     * @return
     */
    @Override
    public NodeReference findSuccessor(Key key) {
        // Each key, nodeRef.nodeId (TODO fix), is stored on the first node
        // whose identifier, nodeId, is equal to or follows nodeRef.nodeId
        // in the identifier space; (TODO no equal sign on second (successor) condition? Needed in only one replica scenario)
        NodeReference closestPrecedingNode = fingerSessionBean.getClosestPrecedingNode(key);

        if (isPredecessor(closestPrecedingNode)) {
            return this.nodeRef;
        }

        if (isLocal(closestPrecedingNode)) {
            // return (successor)
            if (getSuccessor() == null || isLocal(getSuccessor())) {
                return fingerSessionBean.getFirst();
            }

            return getSuccessor().getNodeReference();
        }
        // get the closest preceding node and trigger the findSuccessor (remote)
        return getReference(closestPrecedingNode).findSuccessor(key); // As NodeReference returned
    }

    /****** COMODO METHODS ********/

    /***
     * get a NodeReference (to himSelf or as a RemoteNodeProxy for another peer)
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

    /**
     * if the BaseNode given is himself returns true
     * @param obj
     * @return 
     */
    private boolean isLocal(BaseNode obj) {
        if (obj == null) {
            return false;
        }

        return this.nodeRef.equals(obj.getNodeReference());
    }
    
    /**
     * if the NodeReference given is himself returns true
     * @param obj
     * @return 
     */
    private boolean isLocal(NodeReference obj) {
        if (obj == null) {
            return false;
        }

        return this.nodeRef.equals(obj);
    }

    /**
     * returns true if the NodeReference given is the predecessor
     * @param obj
     * @return 
     */
    private boolean isPredecessor(NodeReference obj) {
        if (obj == null || getPredecessor() == null) {
            return false;
        }

        return getPredecessor().getNodeReference().equals(obj);
    }

    /**
     * Ping/Healthcheck of the predecessor
     * @return
     */
    private boolean checkPredecessor() {
        return getPredecessor().getNodeReference()
                .equals(new RemoteNodeProxy(getPredecessor()
                        .getNodeReference()).ping());
    }

    /***
     * Get the NodeReference of the Predecessor if it is set
     * @return
     */
    @Override
    public NodeReference getPredecessorNodeRef() {
        return (getPredecessor()!= null) ? getPredecessor().getNodeReference() : null;
    }

    /*** GETTERS AND SETTERS ****/

    /**
     * @return the successor
     */
    private BaseNode getSuccessor() {
        return ringSessionBean.getSuccessor();
    }

    /**
     * @param successor the successor to set
     */
    private void setSuccessor(BaseNode successor) {
        ringSessionBean.setSuccessor(successor);
    }

    /**
     * @return the predecessor
     */
    private BaseNode getPredecessor() {
        return ringSessionBean.getPredecessor();
    }

    /**
     * @param predecessor the predecessor to set
     */
    private void setPredecessor(BaseNode predecessor) {
        ringSessionBean.setPredecessor(predecessor);
    }

    /**
     * @return the hasJoined
     */
    private Boolean getHasJoined() {
        return ringSessionBean.getHasJoined();
    }

    /**
     * @param hasJoined the hasJoined to set
     */
    private void setHasJoined(Boolean hasJoined) {
        ringSessionBean.setHasJoined(hasJoined);
    }

}
