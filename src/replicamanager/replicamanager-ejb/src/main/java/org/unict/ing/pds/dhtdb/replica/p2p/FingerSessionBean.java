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
