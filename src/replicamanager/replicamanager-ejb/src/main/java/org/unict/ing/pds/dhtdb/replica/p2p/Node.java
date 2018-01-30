/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
//@Remote(BaseNode.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class Node extends BaseNode {
    private FingerTable fingerTable;
    
    @Resource(type = Storage.class)
    private Storage storage;
    
    @Resource(name="serviceURL", lookup="url/myurl")
    private String serviceURL;
    
    private NodeReference successor, predecessor;

    public Node() {

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

//    public Node(String hostname, short port, NodeID nodeID) {
//        super(hostname, port, nodeID);
//    }
//    
//    public Node(String hostname, NodeID nodeID) {
//        super(hostname, DEFAULT_PORT, nodeID);
//    }  
    
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
    public void put() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void get() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        // Check if this.NodeID < nodeRef.NodeID <= successor.NodeID
        if ((this.nodeID.compareTo(nodeRef.getNodeID()) < 0) && (nodeRef.getNodeID().compareTo(successor.getNodeID()) <= 0))
            // return successor
            return successor;
        else {
            // get the closest preceding node and trigger the findSuccessor
            return fingerTable.getClosestPrecedingNode(nodeRef).findSuccessor(nodeRef);
        }
    }

}
