/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class NodeSessionBean extends BaseNode {
    private FingerTable fingerTable;
    
    @Resource(type = Storage.class)
    private Storage storage;
    
    //@Resource(name="serviceURL", lookup="url/myurl")
    private String serviceURL;
    
    private NodeReference successor, predecessor;
    private long id;
    
    public NodeSessionBean() {

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

//    public NodeSessionBean(String hostname, short port, NodeID nodeID) {
//        super(hostname, port, nodeID);
//    }
//    
//    public NodeSessionBean(String hostname, NodeID nodeID) {
//        super(hostname, DEFAULT_PORT, nodeID);
//    }  
    
    private void init() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println(ip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NodeSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        /*if ((this.nodeID.compareTo(nodeRef.getNodeID()) < 0) && (nodeRef.getNodeID().compareTo(successor.getNodeID()) <= 0))
            // return successor
            return successor;
        else {
            // get the closest preceding node and trigger the findSuccessor
            return fingerTable.getClosestPrecedingNode(nodeRef).findSuccessor(nodeRef);
        }*/
        return null;
    }

}
