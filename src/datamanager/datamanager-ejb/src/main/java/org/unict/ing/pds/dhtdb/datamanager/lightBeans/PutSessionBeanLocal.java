/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;

/**
 *
 */
@Local
public interface PutSessionBeanLocal {

    public void lightPut(GenericStat stat);
    
}
