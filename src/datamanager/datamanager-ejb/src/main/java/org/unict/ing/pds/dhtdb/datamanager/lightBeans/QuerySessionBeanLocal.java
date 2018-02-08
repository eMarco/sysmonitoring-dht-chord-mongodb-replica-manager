/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import java.util.List;
import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Range;

/**
 *
 * @author aleskandro
 */
@Local
public interface QuerySessionBeanLocal {

    public List<GenericValue> getRangeQueryDatas(Range range);
    
}
