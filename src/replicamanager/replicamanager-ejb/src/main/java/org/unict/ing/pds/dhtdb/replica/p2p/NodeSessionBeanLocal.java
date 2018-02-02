/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;
import org.unict.ing.pds.dhtdb.utils.replicamanager.ChordNode;
import org.unict.ing.pds.dhtdb.utils.replicamanager.DHTNode;
import java.util.List;
import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

import org.unict.ing.pds.dhtdb.utils.replicamanager.NodeReference;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Local
public interface NodeSessionBeanLocal extends DHTNode, ChordNode {
    public String myTest();

    public Boolean write(Key k, GenericValue elem);

    public List<GenericValue> lookup(Key k);
    public NodeReference getNodeReference();

    public String myTest2();
}
