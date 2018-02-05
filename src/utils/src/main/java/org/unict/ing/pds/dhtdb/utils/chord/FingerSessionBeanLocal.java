/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.chord;

import java.util.Collection;
import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Local
public interface FingerSessionBeanLocal {

    /**
     *
     * @param node
     */
    void addNode(NodeReference node);

    /**
     *
     * @param key
     * @return
     */
    NodeReference getClosestPrecedingNode(Key key);

    NodeReference getClosestPrecedingNode(NodeReference node);

    /**
     *
     * @return
     */
    NodeReference getFirst();

    /**
     *
     * @return
     */
    NodeReference getLast();

    /**
     *
     * @param tableElements
     */
    void setTable(Collection<NodeReference> tableElements);

    /**
     *
     * @param newTable
     */
    void swapTable(FingerTable newTable);

}
