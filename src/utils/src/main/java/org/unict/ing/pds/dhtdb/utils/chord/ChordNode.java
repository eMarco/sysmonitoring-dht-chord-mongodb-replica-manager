/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.chord;

import java.util.List;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public interface ChordNode {
    public void bootstrap(NodeReference nodeRef);
    public NodeReference notify(NodeReference nodeRef);

    public Boolean put(List<GenericValue> elem);
    public List<GenericValue> getLessThanAndRemove(Key key);

    public NodeReference findSuccessor(Key key);
    public NodeReference findPredecessor(Key key);
    public NodeReference getPredecessorNodeRef();

    public List<GenericValue> delete(Key key);
}
