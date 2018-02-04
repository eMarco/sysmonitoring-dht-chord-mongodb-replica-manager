/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.replicamanager;

import java.util.Collection;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;
import org.unict.ing.pds.dhtdb.utils.replicamanager.NodeReference;
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
     * @param tableElements
     */
    public void replace(Collection<NodeReference> tableElements) {
        table.clear();
        table.addAll(tableElements);
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
//        System.out.println("TABLE " + this.table.toString());
//        System.out.println("LOWER: " + lower);
        if (lower == null)
            return table.last();
        return lower;
    }

    public void addNode(NodeReference node) {
        table.add(node);
    }

    public NodeReference getFirst() {
        return table.first();
    }

    public NodeReference getLast() {
        return table.last();
    }

}
