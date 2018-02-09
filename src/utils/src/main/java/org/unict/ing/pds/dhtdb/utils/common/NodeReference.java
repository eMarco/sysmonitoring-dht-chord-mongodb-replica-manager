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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeReference implements Comparable<NodeReference>, Serializable {
    /**
     * NodeReference.MASTER_NODE is used in this example as the master node known by the peers to join
     * the Chord Overlay network
     */
    public static final NodeReference  MASTER_NODE = new NodeReference("distsystems_replicamanager_1");
    private static final String HOSTNAME_PREFIX = "distsystems_replicamanager_";
    private static final String REMOTE_PORT = "8080";
    private static final String RESOURCES_PATH = "/replicamanager-web/webresources";

    private Key nodeId;
    private String hostname;

    public NodeReference(Key nodeId, String ip) {
        this.nodeId = nodeId;
        this.hostname = ip;
    }

    public NodeReference(String ip) {
        this.nodeId = new Key(ip, true);
        this.hostname = ip;
    }


    public NodeReference() {
    }

    @SuppressWarnings("empty-statement")
    public static NodeReference getLocal() {
        NodeReference nodeRef = new NodeReference();
        try {
            int i = 0;
            nodeRef.hostname   = HOSTNAME_PREFIX;
            // Docker compose workaround to use container_name (IP Address is not static,
            // the hash could be different than an old one for the same replica
            while (!InetAddress.getLocalHost().getHostAddress()
                    .equals(InetAddress.getByName(
                            nodeRef.hostname + ++i).getHostAddress()));
            nodeRef.hostname += i;
            nodeRef.nodeId = new Key(nodeRef.hostname, true);
            System.out.println("[INFO] My Container name is: " + nodeRef.hostname);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NodeReference.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodeRef;
    }

    public Key getNodeId() {
        return nodeId;
    }

    public String getHostname() {
        return hostname;
    }

    public String getEndpoint() {
        return "http://" + hostname + ":" + REMOTE_PORT + RESOURCES_PATH;
    }

    @Override
    public String toString() {
        return hostname + "\t" + nodeId;
    }

    @Override
    public int compareTo(NodeReference o) {
        return this.nodeId.compareTo(o.nodeId);
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
