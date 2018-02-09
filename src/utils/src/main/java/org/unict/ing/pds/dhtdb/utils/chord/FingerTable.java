/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.chord;

import java.util.Collection;
import java.util.TreeSet;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 *
 */
@Lock(LockType.READ)
public class FingerTable {
    private TreeSet<NodeReference> table = new TreeSet<>((NodeReference p1, NodeReference p2) -> p1.compareTo(p2));

    /**
     *
     * @return |
     */
    public TreeSet<NodeReference> getTable() {
        return table;
    }

    /**
     *
     * @param tableElements |
     */
    @Lock(LockType.WRITE)
    public void setTable(Collection<NodeReference> tableElements) {
        table.clear();
        table.addAll(tableElements);
    }

    @Lock(LockType.WRITE)
    public void swapTable(TreeSet<NodeReference> newTable) {
        this.table = newTable;
    }

    /**
     *
     * @param key |
     * @return |
     */
    public NodeReference getClosestPrecedingNode(Key key) {
        return getClosestPrecedingNode(new NodeReference(key, ""));
    }

    /**
     *
     * @param node |
     * @return |
     */
    public NodeReference getClosestPrecedingNode(NodeReference node) {
        NodeReference lower = table.lower(node);
        if (lower == null)
            return table.last();
        return lower;
    }

    /**
     *
     * @param node |
     */
    @Lock(LockType.WRITE)
    public void addNode(NodeReference node) {
        table.add(node);
    }

    /**
     *
     * @return |
     */
    public NodeReference getFirst() {
        return table.first();
    }

    /**
     *
     * @return |
     */
    public NodeReference getLast() {
        return table.last();
    }

}
