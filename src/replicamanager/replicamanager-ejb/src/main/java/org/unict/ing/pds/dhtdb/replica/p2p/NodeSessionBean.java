/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import com.google.gson.Gson;
import java.util.List;
import javax.annotation.PostConstruct;
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
        this.nodeRef     = NodeReference.getLocal();
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
        write(myKey, x);
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

    @Override
    public Boolean put(Key k, GenericValue elem) {
        this.storage.insert(elem, k.toString());
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
    public Boolean write(Key k, GenericValue elem) {
        System.out.println("Trying to write");
        NodeReference theOwner = this.findSuccessor(new NodeReference(k, ""));
        if (theOwner.equals(this.nodeRef))
            return put(k, elem);
        else
            return new RemoteNodeProxy(theOwner).put(k, elem);  
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

        /*System.out.println("FINDSUCCESSOR: ");
        System.out.println("ME: " + this.nodeRef.getNodeId());
        System.out.println("OTHER: " + this.successor.getNodeReference().getNodeId());
        System.out.println("KEY:  " + nodeRef.getNodeId());*/
        if (fingerTable.getClosestPrecedingNode(nodeRef).equals(this.nodeRef)) {
            // return me
            System.out.println("I am the owner of this key's interval");
        
            return this.nodeRef;
        }
        else {
            // get the closest preceding node and trigger the findSuccessor (remote)
            NodeReference remote = fingerTable.getClosestPrecedingNode(nodeRef);
            System.out.println("Looking for a candidate remote node as successor for the given key: " + remote);
            return new RemoteNodeProxy(remote).findSuccessor(nodeRef);
        }
    }
}
