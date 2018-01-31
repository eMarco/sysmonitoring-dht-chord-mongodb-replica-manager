/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import com.google.gson.Gson;
import java.util.List;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.replica.storage.MongoDBStorage;
import org.unict.ing.pds.dhtdb.utils.model.CPUStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;

/**
 *
 * @author aleskandro
 */
@Singleton
public class LocalNodeSessionBean implements LocalNodeSessionBeanLocal {
    private NodeReference thisRef, successor, predecessor;
    private FingerTable   fingerTable;
    private Storage       storage;
    private void init() {
        this.thisRef     = new NodeReference();
        this.fingerTable = new FingerTable();
        this.successor   = this.predecessor = this.thisRef;
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
        return new Gson().toJson(get(thisRef.getNodeId()));
        
        //return findSuccessor(new NodeReference(thisRef.getNodeId(), "")).toString();
    }
    
    private NodeReference successor(Key k) {
        return findSuccessor(new NodeReference(k, ""));
    }
    
    public List<GenericStat> get(Key k) {
        if (findSuccessor(new NodeReference(k, "")).equals(thisRef)) {
            return this.storage.find(k.toString());
        } else {
            //TODO forward to another suitable node using the fingertable
        }
        return null;
    }
    
    public Boolean put(Key k, GenericStat elem) {
        if (findSuccessor(new NodeReference(k, "")).equals(thisRef)) {
            this.storage.insert(elem, k.toString());
            return true;
        } else {
            // TODO Forward to the proper node
            return false;
        }
        
    }
    
    public NodeReference findSuccessor(NodeReference nodeRef) {
        // Each key, nodeRef.nodeId (TODO fix), is stored on the first node 
        //whose identifier, nodeId, is equal to or follows nodeRef.nodeId 
        // in the identifier space; (TODO no equal sign on second (successor) condition? Needed in only one replica scenario)
        if ((this.thisRef.compareTo(nodeRef) <= 0) && (successor.compareTo(nodeRef) >= 0))
            // return successor
            return successor;
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            return fingerTable.getClosestPrecedingNode(nodeRef).findSuccessor(nodeRef);
        }
    }
}
