/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.replicamanager;

import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.replicamanager.BaseNode;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Local
public interface RingSessionBeanLocal {

    /**
     * @return the predecessor
     */
    BaseNode getPredecessor();

    /**
     * @return the successor
     */
    BaseNode getSuccessor();

    /**
     * @param predecessor the predecessor to set
     */
    void setPredecessor(BaseNode predecessor);

    /**
     * @param successor the successor to set
     */
    void setSuccessor(BaseNode successor);

    /**
     * @return the hasJoined
     */
    Boolean getHasJoined();

    /**
     * @param hasJoined the hasJoined to set
     */
    void setHasJoined(Boolean hasJoined);

}
