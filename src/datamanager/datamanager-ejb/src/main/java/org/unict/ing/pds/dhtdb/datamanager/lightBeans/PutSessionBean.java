/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author aleskandro
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
     * @param stat 
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
