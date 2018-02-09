/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

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
import org.unict.ing.pds.dhtdb.utils.chord.FingerSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.common.RemoteNodeProxy;

/**
 * The bean responsible of the low-level communication with the Chord Overlay network
 * @author aleskandro
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class DataManagerChordSessionBean implements DataManagerChordSessionBeanLocal {

    /***
     * CONFIG VARS
     */
    private static final int PERIOD = 30; //seconds

    /***
     * CONFIG VARS END
     */

    @EJB
    private FingerSessionBeanLocal fingerSessionBean;

    @Resource
    private SessionContext context;

    @PostConstruct
    private void init() {
        // Starting chord
        fingerSessionBean.addNode(NodeReference.MASTER_NODE);
        
        TimerService timerService = context.getTimerService();
        timerService.getTimers().forEach((Timer t) -> t.cancel());
        timerService.createIntervalTimer(2020, PERIOD * 1000, new TimerConfig("FINGERS", true));
    }

    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().equals("FINGERS")) {
            this.fixFingers();
        }
    }

    /******** DHT/FUNCTIONAL/STORAGE METHODS ********/

    /***
     * Make a lookup on the Chord Network for the value associated with the given Key
     * @param key
     * @return a List of the values associated with the given Key (see Light for motivations about a List, 
     * that we considers the real value stored in the Chord Network with a Key)
     */
    @Override
    public List<GenericValue> lookup(Key key) {
        return this.getReference(this.findSuccessor(key)).get(key);
    }

    /***
     * Make a put in the Chord Network for the given elem
     * @param key
     * @param elem
     * @return the result of the put
     */
    @Override
    public Boolean write(Key key, GenericValue elem) {
        elem.setKey(key);
        System.out.println("Trying to write");
        return this.getReference(this.findSuccessor(key)).put(elem);
    }
    
    /**
     * Make a put in the Chord Network for a full value (a List) associated with the Key
     * @param key
     * @param elems
     * @return the result of the put
     */
    @Override
    public Boolean write(Key key, List<GenericValue> elems) {
        System.out.println("Trying to write");
        return this.getReference(this.findSuccessor(key)).put(elems);
    }

    /**
     * Make a delete and put in the chord network 
     * (just for simplicity not in a single call to the proxy)
     * for a List of elems
     * @param key
     * @param elems
     * @return 
     */
    @Override
    public Boolean update(Key key, List<GenericValue> elems) {
        System.out.println("Trying to write");
        this.getReference(this.findSuccessor(key)).delete(key);
        return this.getReference(this.findSuccessor(key)).put(elems);
    }

    @Override
    public Boolean update(Key key, GenericValue elem) {
        System.out.println("Trying to write");
        this.getReference(this.findSuccessor(key)).delete(key);
        return this.getReference(this.findSuccessor(key)).put(elem);
    }

    /***
     * implementation of the findSuccessor Chord primitive using the FingerTable
     * and a RemoteNodeProxy (the DataManager is just a client, not in the ring, as Chord protocol permits)
     * @param key
     * @return
     */
    public NodeReference findSuccessor(Key key) {
        NodeReference closestPrecedingNode = fingerSessionBean.getClosestPrecedingNode(key);
        return getReference(closestPrecedingNode).findSuccessor(key); // As NodeReference returned
    }

    /**
     * Get an instance of a RemoteNodeProxy for the NodeReference given
     * @param nodeReference
     * @return RemoteNodeProxy
     */
    private RemoteNodeProxy getReference(NodeReference nodeReference) {
        return new RemoteNodeProxy(nodeReference);
    }

    /***
     * Fix fingers Chord primitive implementation
     * 
     */
    private void fixFingers() {
        TreeSet<NodeReference> newFingerTable = new TreeSet<>();

        newFingerTable.add(NodeReference.MASTER_NODE);
        for (int i = 0; i < Key.LENGTH; i++) {
            Key sumPow = newFingerTable.last().getNodeId().sumPowDivided(Key.LENGTH, Key.LENGTH);
            NodeReference succ = findSuccessor(sumPow);
            newFingerTable.add(succ);
        }

        fingerSessionBean.swapTable(newFingerTable);
    }
}
