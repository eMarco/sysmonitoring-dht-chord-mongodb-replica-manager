/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.utils.chord.FingerSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.chord.FingerTable;

/**
 * See FingerTable class, this Bean is a Singleton the is responsible for the FingerTable
 * of the DataManager, the implementation is the same for DataManager (not in the ring)
 * and any ReplicaManager (in the ring)
 * 
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class FingerSessionBean extends FingerTable implements FingerSessionBeanLocal {

}
