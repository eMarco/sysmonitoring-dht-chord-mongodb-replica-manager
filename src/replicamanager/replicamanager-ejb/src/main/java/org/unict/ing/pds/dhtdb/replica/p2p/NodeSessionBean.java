/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import com.google.gson.Gson;
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
    private NodeReference thisRef;
    private RemoteNodeProxy successor, predecessor;
    private FingerTable   fingerTable;
    private Storage       storage;
        
    public NodeSessionBean() {

    }
    
    private void init() {
        this.thisRef     = new NodeReference(new Key("asd"), "172.18.0.3");
        this.fingerTable = new FingerTable();
        this.successor   = this.predecessor = new RemoteNodeProxy(this.thisRef, this.thisRef.getNodeId());
        this.fingerTable.addNode(thisRef);
        this.storage = new MongoDBStorage();
    }
    
    // triggered by http://localhost:8081/replicamanager-web/webresources/generic
    public String myTest() {
        this.init();
        //return this.thisRef.toString();
        CPUStat x = new CPUStat((float)0.4, 4, "asd");
        
        //return new Gson().toJson(x);
        // Using this node's id as key, just for tests
        
        put(thisRef.getNodeId(), x);
        System.out.println("DB: " + new Gson().toJson(get(thisRef.getNodeId())));
//        return new Gson().toJson(get(thisRef.getNodeId()));
        RemoteNodeProxy thisRefRemote = new RemoteNodeProxy(thisRef, thisRef.getNodeId());
        
        List<GenericStat> ret = thisRefRemote.get(thisRef.getNodeId());
        System.out.println("GOT " + ret.toString());
        
        return new Gson().toJson(ret);
//        return findSuccessor(new NodeReference(thisRef.getNodeId(), "")).toString();
    }

    private long successor(long k) {
        return 0;
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
    
    @Override
    public Boolean put(Key k, GenericStat elem) {
        if (findSuccessor(new NodeReference(k, "")).equals(thisRef)) {
            this.storage.insert(elem, k.toString());
            return true;
        } else {
            // TODO Forward to the proper node
            return false;
        }
        
    }

    @Override
    public List<GenericStat> get(Key k) {
        if (findSuccessor(new NodeReference(k, "")).equals(thisRef)) {
            System.out.println("SEARCHING DB FOR KEY: " + k.toString());
            
            System.out.println("FOUND " + this.storage.find(k.toString()).toString());
            return this.storage.find(k.toString());
        } else {
            //TODO forward to another suitable node using the fingertable
        }
        return null;
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
        //whose identifier, nodeId, is equal to or follows nodeRef.nodeId 
        // in the identifier space; (TODO no equal sign on second (successor) condition? Needed in only one replica scenario)
        if ((this.thisRef.compareTo(nodeRef) <= 0) && (successor.getNodeRef().compareTo(nodeRef) >= 0))
            // return successor
            return successor.getNodeRef();
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            return fingerTable.getClosestPrecedingNode(nodeRef).findSuccessor(nodeRef);
        }
    }

}
