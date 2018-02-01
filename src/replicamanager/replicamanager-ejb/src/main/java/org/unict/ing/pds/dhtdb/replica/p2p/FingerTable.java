/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.TreeSet;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class FingerTable {
    private final TreeSet<NodeReference> table = new TreeSet<>((NodeReference p1, NodeReference p2) -> p1.compareTo(p2));

    /**
     *
     * @return
     */
    public TreeSet<NodeReference> getTable() {
        return table;
    }

    /**
     * 
     * @param key
     * @return 
     */    
    public NodeReference getClosestPrecedingNode(Key key) {
        return getClosestPrecedingNode(new NodeReference(key, ""));
    }
    
    /**
     * 
     * @param node
     * @return 
     */    
    public NodeReference getClosestPrecedingNode(NodeReference node) {
        NodeReference lower = table.lower(node);
        if (lower == null)
            return table.last();
        return lower;
    }
    
    public void addNode(NodeReference node) {
        table.add(node);
    }
    
}
