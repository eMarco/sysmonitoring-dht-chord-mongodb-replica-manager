/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import org.unict.ing.pds.dhtdb.utils.chord.FingerSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.chord.FingerTable;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class FingerSessionBean extends FingerTable implements FingerSessionBeanLocal {

}
