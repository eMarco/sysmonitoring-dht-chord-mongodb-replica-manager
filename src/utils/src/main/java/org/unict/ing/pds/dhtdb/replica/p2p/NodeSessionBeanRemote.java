/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.List;
import javax.ejb.Remote;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Remote
public interface NodeSessionBeanRemote extends DHTNode, ChordNode {
    public String myTest();

    public Boolean write(Key k, GenericValue elem);

    public List<GenericValue> lookup(Key k);
}
