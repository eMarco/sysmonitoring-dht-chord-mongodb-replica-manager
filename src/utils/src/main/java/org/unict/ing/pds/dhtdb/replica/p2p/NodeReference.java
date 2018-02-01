/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NodeReference implements Comparable<NodeReference>, Serializable {
    
    private Key nodeId;
    private String hostname;

    public NodeReference(Key nodeId, String ip) {
        this.nodeId = nodeId;
        this.hostname = ip;
    }
    
    public NodeReference(String ip) {
        this.nodeId = new Key(ip);
        this.hostname = ip;
    }

    @SuppressWarnings("empty-statement")
    public NodeReference() {
        try {
            int i = 0;
            hostname   = "distsystems_replicamanager_";
            // Docker compose workaround to use container_name (IP Address is not static, 
            // the hash could be different than an old one for the same replica
            while (!InetAddress.getLocalHost().getHostAddress()
                    .equals(InetAddress.getByName(
                            hostname + ++i).getHostAddress()));
            hostname += i;
            nodeId = new Key(hostname);
            System.out.println("[INFO] My Container name is: " + hostname);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NodeReference.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Key getNodeId() {
        return nodeId;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public String toString() {
        return hostname + "\t" + nodeId;
    }
    
    public String getEndpoint() {
        return "http://" + hostname + ":8080";
    }

    @Override
    public int compareTo(NodeReference o) {
        return this.nodeId.compareTo(o.nodeId);
    }

    public NodeReference findSuccessor(NodeReference nodeRef) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeReference other = (NodeReference) obj;
        if (!Objects.equals(this.nodeId, other.nodeId)) {
            return false;
        }
        return true;
    }
    
}
