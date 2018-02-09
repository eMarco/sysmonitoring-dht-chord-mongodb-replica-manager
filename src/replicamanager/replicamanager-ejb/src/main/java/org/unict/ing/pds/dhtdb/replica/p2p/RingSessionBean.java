/* 
 * Copyright (C) 2018 aleskandro - eMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * Singleton Bean that manages the successor and predecessor fields
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
     * @return | the successor
     */
    @Override
    public BaseNode getSuccessor() {
        return successor;
    }

    /**
     * @param successor the successor to set |
     */
    @Lock(LockType.WRITE)
    @Override
    public void setSuccessor(BaseNode successor) {
        this.successor = successor;
    }

    /**
     * @return | the predecessor
     */
    @Override
    public BaseNode getPredecessor() {
        return predecessor;
    }

    /**
     * @param predecessor the predecessor to set |
     */
    @Lock(LockType.WRITE)
    @Override
    public void setPredecessor(BaseNode predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @return | the hasJoined
     */
    @Override
    public Boolean getHasJoined() {
        return hasJoined;
    }

    /**
     * @param hasJoined the hasJoined to set |
     */
    @Lock(LockType.WRITE)
    @Override
    public void setHasJoined(Boolean hasJoined) {
        this.hasJoined = hasJoined;
    }
}
