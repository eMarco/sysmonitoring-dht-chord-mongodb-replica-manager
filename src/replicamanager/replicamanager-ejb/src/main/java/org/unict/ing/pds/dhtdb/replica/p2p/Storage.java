/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.List;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public interface Storage {

    public List<GenericValue> find(Key key);
    public void remove(Key key);
    public void update(GenericValue elem, Key key);

    public void insert(GenericValue elem);

    public void insertMany(List<GenericValue> elem);
    public List<GenericValue> lessThanAndRemove(Key key);
}
