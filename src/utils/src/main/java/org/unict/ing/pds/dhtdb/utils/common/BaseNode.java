/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.common;

import javax.ejb.Remote;
import org.unict.ing.pds.dhtdb.utils.chord.ChordNode;
import org.unict.ing.pds.dhtdb.utils.dht.DHTNode;

/**
 *
 */
@Remote
public abstract class BaseNode implements DHTNode, ChordNode {
    private static final String MASTER_NODE = "distsystems_replicamanager_1";
    protected NodeReference   nodeRef;
    public BaseNode() {
    }

    public BaseNode(NodeReference ref) {
        this.nodeRef = ref;
    }

    public NodeReference getNodeReference() {
        return nodeRef;
    }


}
