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
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 * This Bean is responsible for the PUT operations over LIGHT
 */
@Stateless
public class PutSessionBean implements PutSessionBeanLocal {
    
    
    @EJB
    private LightSessionBeanLocal lightSessionBean;
    
    @EJB
    private LookupSessionBeanLocal lookupSessionBean;

    @EJB
    private DataManagerChordSessionBeanLocal dataManagerChordSessionBean;

    /**
     * Put a new GenericStat in the Distributed Database
     * Eventually it calls LightSessionBean.splitAndPut to split the tree if the
     * Bucket has a recordsCounter greater or equal to LightSessionBean.TETA_SPLIT
     * @param stat  |
     */
    @Override
    public void lightPut(GenericStat stat) {
        System.err.println("PUT!");
        long timestamp = stat.getTimestamp();
        Bucket dhtKey   = lookupSessionBean.lightLabelLookup(timestamp);
        if (dhtKey == null) { // The database is empty
            // Creates a new bucket
            Bucket theFirst = new Bucket(Range.REPRESENTABLE_RANGE, new Label("#0"), 0);
            dataManagerChordSessionBean.write(theFirst.getKey(), theFirst);
            lightPut(stat);
            return;
        }
        Bucket bucket  = dhtKey;
        if (bucket.getRecordsCounter() >= LightSessionBean.TETA_SPLIT) {
            dhtKey = lightSessionBean.splitAndPut(bucket, timestamp, stat);
            lightSessionBean.checkTreeHeight(dhtKey.getLeafLabel());
        } else {
            bucket.incrementRecordsCounter();
            dataManagerChordSessionBean.update(bucket.getKey(), bucket);
        }
        
        stat.setKey(dhtKey.getLeafLabel().toDataKey());
        dataManagerChordSessionBean.write(stat.getKey(), stat);
    }

}
