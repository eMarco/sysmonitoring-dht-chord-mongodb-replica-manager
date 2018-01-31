/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NodeReference implements Comparable<NodeReference> {
    
    private Key nodeId;
    private String ip;

    public NodeReference(Key nodeId, String ip) {
        this.nodeId = nodeId;
        this.ip = ip;
    }
    
    public NodeReference(String ip) {
        this.nodeId = new Key(ip);
        this.ip = ip;
    }

    public NodeReference() {
        try {
            this.ip   = InetAddress.getLocalHost().getHostAddress();
            this.nodeId = new Key(ip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NodeReference.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Key getNodeId() {
        return nodeId;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return nodeId + "\t" + ip;
    }

    @Override
    public int compareTo(NodeReference o) {
        return this.nodeId.compareTo(o.nodeId);
    }

    NodeReference findSuccessor(NodeReference nodeRef) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
