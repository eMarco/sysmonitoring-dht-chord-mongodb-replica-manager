/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.List;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public interface DHTNode {

    /**
     *
     */
    public Boolean put(Key k, GenericStat elem);

    /**
     *
     * @param key
     */
    public List<GenericStat> get(Key k);
}
