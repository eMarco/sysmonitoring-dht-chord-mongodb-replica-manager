/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public interface ChordNode {
    public void bootstrap(NodeReference nodeRef);
    
    public NodeReference findSuccessor(Key key);
}
