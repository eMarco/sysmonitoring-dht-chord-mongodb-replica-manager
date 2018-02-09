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
 *
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


    // Put a new GenericStat in the Database
    @Override
    public void lightPut(GenericStat stat) {
        
        System.err.println("PUT PUT");
        long timestamp = stat.getTimestamp();
        Bucket dhtKey   = lookupSessionBean.lightLabelLookup(timestamp);
        if (dhtKey == null) { // The database is empty
            // Creates a new bucket
            Bucket theFirst = new Bucket(Range.REPRESENTABLE_RANGE, new Label("#0"), 0);
            System.err.println("#0");
            System.err.println(new Label("#0").toKey());
            System.err.println(new Label("#0").toDHTKey().toKey());
            System.err.println(theFirst.getKey());
            dataManagerChordSessionBean.write(theFirst.getKey(), theFirst);
            lightPut(stat);
            return;
        }
        System.err.println("GET THE BUCKET FOR " + dhtKey + " AFTER LIGHT LOOKUP");
        Bucket bucket  = dhtKey;
        System.err.println("BUCKET NEWLY " + bucket);
        if (bucket.getRecordsCounter() >= LightSessionBean.TETA_SPLIT) {
            dhtKey = lightSessionBean.splitAndPut(bucket, timestamp, stat);
            lightSessionBean.checkTreeHeight(dhtKey.getLeafLabel());
            //return;
        } else {
            bucket.incrementRecordsCounter();
            dataManagerChordSessionBean.update(bucket.getKey(), bucket);
        }
        
        stat.setKey(dhtKey.getLeafLabel().toDataKey());
        dataManagerChordSessionBean.write(stat.getKey(), stat);
    }

}
