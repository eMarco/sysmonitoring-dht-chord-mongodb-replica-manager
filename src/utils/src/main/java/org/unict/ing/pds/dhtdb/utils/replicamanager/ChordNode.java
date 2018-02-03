/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.replicamanager;

import java.util.List;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public interface ChordNode {
    public void bootstrap(NodeReference nodeRef);
    
    public NodeReference findSuccessor(Key key);
    
    public NodeReference notify(NodeReference nodeRef);

    public NodeReference getPredecessor();

    public Boolean put(List<GenericValue> elem); 
    public List<GenericValue> getLessThanAndRemove(Key key);

    public NodeReference findPredecessor(Key key);
}
