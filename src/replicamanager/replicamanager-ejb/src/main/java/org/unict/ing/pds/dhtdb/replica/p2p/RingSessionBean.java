/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.utils.common.BaseNode;
import org.unict.ing.pds.dhtdb.utils.chord.RingSessionBeanLocal;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class RingSessionBean implements RingSessionBeanLocal {

    private Boolean hasJoined;
    private BaseNode successor, predecessor;

    @PostConstruct
    private void init() {
        setHasJoined(false);
        setPredecessor(null);
        setSuccessor(null);
    }

    /**
     * @return the successor
     */
    @Override
    public BaseNode getSuccessor() {
        return successor;
    }

    /**
     * @param successor the successor to set
     */
    @Lock(LockType.WRITE)
    @Override
    public void setSuccessor(BaseNode successor) {
        this.successor = successor;
    }

    /**
     * @return the predecessor
     */
    @Override
    public BaseNode getPredecessor() {
        return predecessor;
    }

    /**
     * @param predecessor the predecessor to set
     */
    @Lock(LockType.WRITE)
    @Override
    public void setPredecessor(BaseNode predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @return the hasJoined
     */
    @Override
    public Boolean getHasJoined() {
        return hasJoined;
    }

    /**
     * @param hasJoined the hasJoined to set
     */
    @Lock(LockType.WRITE)
    @Override
    public void setHasJoined(Boolean hasJoined) {
        this.hasJoined = hasJoined;
    }
}
