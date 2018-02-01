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
        
        //put(nodeRef.getNodeId(), x);
        //System.out.println("DB: " + new Gson().toJson(get(nodeRef.getNodeId())));
        //return new Gson().toJson(get(nodeRef.getNodeId()));
        //RemoteNodeProxy thisRefRemote = new RemoteNodeProxy(nodeRef);
        
        //List<GenericStat> ret = thisRefRemote.get(nodeRef.getNodeId());
        //System.out.println("GOT " + ret.toString());
        
        //return new Gson().toJson(ret);
//        return findSuccessor(new NodeReference(thisRef.getNodeId(), "")).toString();

        int idToAdd = 1;
        if (this.nodeRef.getHostname().equals("distsystems_replicamanager_1"))
            idToAdd = 2;
        
        String node2 = "distsystems_replicamanager_" + idToAdd;
        NodeReference theOtherNode = new NodeReference(node2);
        this.fingerTable.addNode(theOtherNode);
        this.successor = this.predecessor = new RemoteNodeProxy(theOtherNode);
        Key myKey = new Key(x.toString());
        write(myKey, x);
        return new Gson().toJson(lookup(nodeRef.getNodeId()));
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
        System.out.println("ASASASASASASS" + k.getId());
        System.out.println("ASDASASASAS" + this.nodeRef.getNodeId().getId());
        System.out.println(successor(k).getNodeId());
        return true;
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
            // The returned list has length 0 or more
            return this.storage.find(k.toString());
        }
        // The client asked the wrong node for the given key
        return null;
    }
    
    // Acting as a client (TODO move to the right class)
    // Check if this.nodeRef is responsible for the given k or forward until the
    // proper node is found to return the result
    @Override
    public List<GenericStat> lookup(Key k) {
        List<GenericStat> ret = get(k);
        if (ret == null) {
            RemoteNodeProxy n = new RemoteNodeProxy(successor(k));
            while ((ret = n.get(k)) == null)
                n = new RemoteNodeProxy(n.findSuccessor(new NodeReference(k,"")));
        }
        return ret;
    }
    
    // Acting as a client (TODO move to the right class)
    // Check if this.nodeRef is responsible for the given k or forward until the
    // proper node is found to return the result
    @Override
    public Boolean write(Key k, GenericStat elem) {
        if (!put(k, elem)) {
            RemoteNodeProxy n = new RemoteNodeProxy(successor(k));
            
            while (!(n.getNodeReference().equals(this.nodeRef) || n.put(k, elem)))
                n = new RemoteNodeProxy(n.findSuccessor(new NodeReference(k,"")));
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
        if ((this.nodeRef.compareTo(nodeRef) <= 0) && (successor.getNodeReference().compareTo(nodeRef) >= 0))
            // return successor
            return successor.getNodeReference();
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            return new RemoteNodeProxy(fingerTable.getClosestPrecedingNode(nodeRef)).findSuccessor(nodeRef);
        }
    }
}
