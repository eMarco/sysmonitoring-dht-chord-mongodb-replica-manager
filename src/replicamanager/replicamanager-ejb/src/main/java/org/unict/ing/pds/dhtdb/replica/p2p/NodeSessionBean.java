/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.commons.codec.digest.DigestUtils;
import org.unict.ing.pds.dhtdb.replica.storage.MongoDBStorage;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
//@Remote(BaseNode.class)
//@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
public class NodeSessionBean extends BaseNode implements NodeSessionBeanRemote {
    
    private RemoteNodeProxy successor, predecessor;
    private FingerTable     fingerTable;
    private Storage         storage;
        
    public NodeSessionBean() {
        //init();
    }
    
    @PostConstruct
    private void init() {
        this.nodeRef     = new NodeReference();
        this.fingerTable = new FingerTable();
        this.successor   = this.predecessor = new RemoteNodeProxy(this.nodeRef);
        
        this.fingerTable.addNode(nodeRef);
        this.storage = new MongoDBStorage();
        int idToAdd = 1;
        if (this.nodeRef.getHostname().equals("distsystems_replicamanager_1"))
            idToAdd = 2;
        
        String node2 = "distsystems_replicamanager_" + idToAdd;
        NodeReference theOtherNode = new NodeReference(node2);
        this.fingerTable.addNode(theOtherNode);
        this.successor = this.predecessor = new RemoteNodeProxy(theOtherNode);
        System.out.println("INIT: ME: " + this.nodeRef.getNodeId());
        System.out.println("INIT: SUCCESSOR: " + this.successor.getNodeReference().getNodeId() + this.successor.getNodeReference().getHostname());
    }
    
    // triggered by http://localhost:8081/replicamanager-web/webresources/generic
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

        Key myKey = new Key(x.toString());
        Key myKey2 = new Key(y.toString());
        //write(myKey, x);
        //write(myKey2, y);
        return new Gson().toJson(lookup(myKey));
    }

    private NodeReference successor(Key k) {
        return findSuccessor(new NodeReference(k, ""));
    }
    private void stabilize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void fixFingers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void join() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /*private boolean myKey(Key k) {
        return true;
        /*NodeReference keySuccessor = findSuccessor(new NodeReference(k, ""));
        System.out.println("SUCCESSOR: " + keySuccessor.toString());
        
        System.out.println((keySuccessor.equals(nodeRef)) ? "ME" : "NOT ME");
        //return keySuccessor.equals(nodeRef);*/
        
    /*}*/

    @Override
    public Boolean put(Key k, GenericStat elem) {
        //if (myKey(k)) {
            this.storage.insert(elem, k.toString());
        //    return true;
        //}
        // The client asked the wrong node for the given key
        return false;
    }

    @Override
    public List<GenericStat> get(Key k) {
        //if (myKey(k)) {
            System.out.println("SEARCHING DB FOR KEY: " + k.toString());
            System.out.println("FOUND " + this.storage.find(k.toString()).toString());
            // The returned list has length 0 or more
            return this.storage.find(k.toString());
        //}
        // The client asked the wrong node for the given key
        //return null;
    }
    
    // Acting as a client (TODO move to the right class)
    // Check if this.nodeRef is responsible for the given k or forward until the
    // proper node is found to return the result
    @Override
    public List<GenericStat> lookup(Key k) {
        System.out.println("LOOKUP!!!!");
        NodeReference theOwner = this.findSuccessor(new NodeReference(k, ""));
        if (theOwner.equals(this.nodeRef))
            return get(k);
        else
            return new RemoteNodeProxy(theOwner).get(k);        
    }
    
    // Acting as a client (TODO move to the right class)
    // Check if this.nodeRef is responsible for the given k or forward until the
    // proper node is found to return the result
    @Override
    public Boolean write(Key k, GenericStat elem) {
       System.out.println("Trying to write");
        if (!put(k, elem)) {
            System.out.println("Can't write here because another node is responsible for this k");
            RemoteNodeProxy n = new RemoteNodeProxy(successor(k));
            System.out.println("The responsible node is: " + n.getNodeReference().getNodeId());
            n.put(k, elem);
        }
        return true;
    }

    @Override
    public void bootstrap(NodeReference nodeRef) {
//        nodeRef.bootstrap(node);
    }

    /***
     * 
     * @param nodeRef
     * @return 
     */
    @Override
    public NodeReference findSuccessor(NodeReference nodeRef) {
        // Each key, nodeRef.nodeId (TODO fix), is stored on the first node 
        // whose identifier, nodeId, is equal to or follows nodeRef.nodeId 
        // in the identifier space; (TODO no equal sign on second (successor) condition? Needed in only one replica scenario)
        //this.init();
        System.out.println("FINDSUCCESSOR: ");
        System.out.println("ME: " + this.nodeRef.getNodeId());
        System.out.println("OTHER: " + this.successor.getNodeReference().getNodeId());
        System.out.println("KET:  " + nodeRef.getNodeId());
        if (fingerTable.getClosestPrecedingNode(nodeRef).equals(this.nodeRef)) {
            // return me
            System.out.println("I am the owner of this key's interval");
        
            return this.nodeRef;
        }
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            NodeReference remote = fingerTable.getClosestPrecedingNode(nodeRef);
            System.out.println("Looking for a candidate remote node as successor for the given key: " + remote.getNodeId() + remote.getHostname());
            return new RemoteNodeProxy(remote).findSuccessor(nodeRef);
        }
    }
}
