/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.List;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public interface Storage {
    public List<GenericValue> find(String primaryKey);
    public void insert(GenericValue elem);

    public void update(GenericValue elem, String primaryKey);
    public void remove(String primaryKey);
    public List<String> getTopics();
    
    public void insertMany(List<GenericValue> elem);
    public List<GenericValue> lessThanAndRemove(String primaryKey);
}
