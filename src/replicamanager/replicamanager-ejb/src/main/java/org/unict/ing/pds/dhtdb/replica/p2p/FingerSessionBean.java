/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.utils.chord.FingerSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.chord.FingerTable;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.common.NodeReference;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class FingerSessionBean implements FingerSessionBeanLocal {

    private FingerTable fingerTable;

    @PostConstruct
    private void init() {
        swapTable(new FingerTable());
    }

    /**
     *
     * @param tableElements
     */
    @Lock(LockType.WRITE)
    @Override
    public void setTable(Collection<NodeReference> tableElements) {
        fingerTable.setTable(tableElements);
    }

    /**
     *
     * @param newTable
     */
    @Lock(LockType.WRITE)
    @Override
    public void swapTable(FingerTable newTable) {
        this.fingerTable = newTable;
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    public NodeReference getClosestPrecedingNode(Key key) {
        return fingerTable.getClosestPrecedingNode(key);
    }

    @Override
    public NodeReference getClosestPrecedingNode(NodeReference node) {
        return fingerTable.getClosestPrecedingNode(node);
    }

    /**
     *
     * @param node
     */
    @Lock(LockType.WRITE)
    @Override
    public void addNode(NodeReference node) {
        fingerTable.addNode(node);
    }

    /**
     *
     * @return
     */
    @Override
    public NodeReference getFirst() {
        return fingerTable.getFirst();
    }

    /**
     *
     * @return
     */
    @Override
    public NodeReference getLast() {
        return fingerTable.getLast();
    }

}
