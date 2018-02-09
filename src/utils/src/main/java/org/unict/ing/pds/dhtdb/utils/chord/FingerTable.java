/* 
 * Copyright (C) 2018 aleskandro - eMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
