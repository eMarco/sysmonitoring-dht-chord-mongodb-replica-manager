/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import java.util.List;
import javax.ejb.Local;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 *
 * @author aleskandro
 */
@Local
public interface DataManagerChordSessionBeanLocal {

    public Boolean write(Key key, GenericValue elem);

    public List<GenericValue> lookup(Key key);

    public Boolean write(Key key, List<GenericValue> elems);

    public Boolean update(Key key, List<GenericValue> elems);

    public Boolean update(Key key, GenericValue elem);
    
}
