/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.Objects;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public final class NodeID implements Comparable<NodeID> {
    private final int id;

    public NodeID(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
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
        final NodeID other = (NodeID) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int compareTo(NodeID o) {
        // TODO : Add modulo?
        return Integer.compare(this.id, o.id);
    }   
}
