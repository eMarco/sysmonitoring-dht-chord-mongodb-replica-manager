/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import javax.ejb.Singleton;

/**
 *
 * @author aleskandro
 */
@Singleton
public class LocalNodeSessionBean implements LocalNodeSessionBeanLocal {
    private NodeReference thisRef, successor, predecessor;
    private FingerTable   fingerTable;
    
    private void init() {
        this.thisRef     = new NodeReference();
        this.fingerTable = new FingerTable();
    }
    
    public String myTest() {
        this.init();
        return this.thisRef.toString();
    }
    
    public NodeReference findSuccessor(NodeReference nodeRef) {
        if ((this.thisRef.compareTo(nodeRef) < 0) && (nodeRef.compareTo(successor) <= 0))
            // return successor
            return successor;
        else {
            // get the closest preceding node and trigger the findSuccessor
            return fingerTable.getClosestPrecedingNode(nodeRef).findSuccessor(nodeRef);
        }
    }
}
