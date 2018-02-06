/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateful;

/**
 *
 * @author aleskandro
 */
@Stateful
@Lock(LockType.READ)
public class LightSessionBean implements LightSessionBeanLocal {

    private int treeHeight = 2;

    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

    @Lock(LockType.WRITE)
    @Override
    public void setTreeHeight(int treeHeight) {
        this.treeHeight = treeHeight;
    }

}
