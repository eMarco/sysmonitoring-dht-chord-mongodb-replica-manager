/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal;
import org.unict.ing.pds.dhtdb.datamanager.LightSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.common.JsonHelper;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 *
 * @author aleskandro
 */
@Stateless
public class PutSessionBean implements PutSessionBeanLocal {
    private static final int TETA_SPLIT = 100;
    
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
        if (bucket.getRecordsCounter() >= TETA_SPLIT) {
            dhtKey = this.splitAndPut(bucket, timestamp, stat);
            //return;
        } else {
            bucket.incrementRecordsCounter();
            dataManagerChordSessionBean.update(bucket.getKey(), bucket);
        }
        
        stat.setKey(dhtKey.getLeafLabel().toDataKey());
        dataManagerChordSessionBean.write(stat.getKey(), stat);
    }

    private Bucket splitAndPut(Bucket localBucket, long timestamp, GenericStat elem) {
        System.err.println("SPLITTING");
        Label localLabel = localBucket.getLeafLabel();
        Range localRange = localBucket.getRange();
        int   currentRecords = localBucket.getRecordsCounter();
        List<GenericValue> currentDatas = lookupSessionBean.lightLookupAndGetDataBucket(localLabel);
        long mid = localRange.createSplit(false).getUpper();
        
        Bucket newLocalBucket;
        Bucket newRemoteBucket;

        // Just two pointers (TODO improve me)
        Bucket leftPointer;
        Bucket rightPointer;
        List<GenericValue> leftDatas   = new LinkedList<>();
        List<GenericValue> rightDatas  = new LinkedList<>();
       
        //List<GenericValue> records = dataManagerChordSessionBean.lookup(localLabel.toDataKey());
        //Bucket remoteBucket = new Bucket();
        if (localLabel.isRight()) {
            newRemoteBucket = leftPointer  = new Bucket(localRange.createSplit(false), localLabel.leftChild(), 0);
            newLocalBucket  = rightPointer = new Bucket(localRange.createSplit(true),  localLabel.rightChild(), 0);
        } else { // isLeft
            newLocalBucket  = leftPointer = new Bucket(localRange.createSplit(false), localLabel.leftChild(), 0);
            newRemoteBucket = rightPointer  = new Bucket(localRange.createSplit(true),  localLabel.rightChild(), 0);
        }
        
        System.err.println("Local Bucket");
        System.err.println(JsonHelper.write(localBucket));
        System.err.println(JsonHelper.write(newLocalBucket));
        System.err.println("Remote bucket");
        System.err.println(JsonHelper.write(newRemoteBucket));
        lightSessionBean.checkTreeHeight(leftPointer.getLeafLabel());
        currentDatas.forEach((GenericValue e) -> {
            if (e instanceof GenericStat)
                if (((GenericStat) e).getTimestamp() >= mid) {
                    e.setKey(rightPointer.getLeafLabel().toDataKey());
                    rightDatas.add(e);
                } else {
                    e.setKey(leftPointer.getLeafLabel().toDataKey());
                    leftDatas.add(e);
                }
        });
        
        leftPointer.setRecordsCounter(leftDatas.size());
        rightPointer.setRecordsCounter(rightDatas.size());
        
        System.err.println("Left Datas");
        System.err.println(leftDatas);
        System.err.println("Right Datas");
        System.err.println(rightDatas);
        // update localBucket
        dataManagerChordSessionBean.update(newLocalBucket.getKey(), newLocalBucket);
        
        // put bucket to remoteBucket
        dataManagerChordSessionBean.write(newRemoteBucket.getKey(), newRemoteBucket);

        // TODO
        // put datas (One of the children should not need to put the entire bucket but we need a way to 
        // delete only one half of the datas associated with a given key derived from the localBucket label
        dataManagerChordSessionBean.write(leftPointer.getLeafLabel().toDataKey(), leftDatas);
        dataManagerChordSessionBean.write(rightPointer.getLeafLabel().toDataKey(), rightDatas);
        
        //return the label where the put has to send the new stat
        if (timestamp > mid) {
            return rightPointer;
        } 
        return leftPointer; 
    }
}
