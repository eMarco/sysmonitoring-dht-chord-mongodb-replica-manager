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
