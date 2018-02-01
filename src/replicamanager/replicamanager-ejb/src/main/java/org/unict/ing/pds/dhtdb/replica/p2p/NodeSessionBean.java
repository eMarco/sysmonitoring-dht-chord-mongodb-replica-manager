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
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.replica.storage.MongoDBStorage;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
//@Remote(BaseNode.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class NodeSessionBean extends BaseNode implements NodeSessionBeanRemote {
    
    private RemoteNodeProxy successor, predecessor;
    private FingerTable     fingerTable;
    private Storage         storage;
        
    public NodeSessionBean() {

    }
    
    private void init() {
        this.nodeRef     = new NodeReference();
        this.fingerTable = new FingerTable();
        this.successor   = this.predecessor = new RemoteNodeProxy(this.nodeRef);
        this.fingerTable.addNode(nodeRef);
        this.storage = new MongoDBStorage();
    }
    
    // triggered by http://localhost:8081/replicamanager-web/webresources/generic
    public String myTest() {
        this.init();
        //return this.thisRef.toString();
        CPUStat x = new CPUStat((float)0.4, 4, "asd");
        
        //return new Gson().toJson(x);
        // Using this node's id as key, just for tests
        
        put(nodeRef.getNodeId(), x);
        //System.out.println("DB: " + new Gson().toJson(get(nodeRef.getNodeId())));
        return new Gson().toJson(get(nodeRef.getNodeId()));
        //RemoteNodeProxy thisRefRemote = new RemoteNodeProxy(nodeRef);
        
        //List<GenericStat> ret = thisRefRemote.get(nodeRef.getNodeId());
        //System.out.println("GOT " + ret.toString());
        
        //return new Gson().toJson(ret);
//        return findSuccessor(new NodeReference(thisRef.getNodeId(), "")).toString();
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
    
    private boolean myKey(Key k) {
        NodeReference keySuccessor = findSuccessor(new NodeReference(k, ""));
        return keySuccessor.equals(nodeRef);
    }
    @Override
    public Boolean put(Key k, GenericStat elem) {
        if (myKey(k)) {
            this.storage.insert(elem, k.toString());
            return true;
        }
        // The client asked the wrong node for the given key
        return false;
    }

    @Override
    public List<GenericStat> get(Key k) {
        if (myKey(k)) {
            System.out.println("SEARCHING DB FOR KEY: " + k.toString());
            System.out.println("FOUND " + this.storage.find(k.toString()).toString());
            
            return this.storage.find(k.toString());
        }
        // The client asked the wrong node for the given key
        return new ArrayList();
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
        if ((this.nodeRef.compareTo(nodeRef) <= 0) && (successor.getNodeReference().compareTo(nodeRef) >= 0))
            // return successor
            return successor.getNodeReference();
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            return fingerTable.getClosestPrecedingNode(nodeRef).findSuccessor(nodeRef);
        }
    }

}
