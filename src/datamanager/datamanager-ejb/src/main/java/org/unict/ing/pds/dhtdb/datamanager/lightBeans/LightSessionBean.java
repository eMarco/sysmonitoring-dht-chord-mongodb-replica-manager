/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager.lightBeans;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.light.utils.Bucket;
import org.unict.ing.pds.light.utils.Label;
import org.unict.ing.pds.light.utils.Range;

/**
 * This beans is a singleton. It stores the height of the tree in runtime. 
 * The height is also stored in the MASTER_NODE to provide a "safe" boot
 * (This is just a workaround, there are several ways to estimate in a distributed
 * way the height of this kind of tree, see PHT).
 * The MASTER_NODE could be replicated, the fault tolerance about its datas is provided
 * by the overlay network.
 * 
 * Still, this singleton is responsible for the operations of tree splitting. These are done
 * in LockWrite so that concurrent put doesn't make the distributed database inconsistent
 * 
 */
@Singleton
@Lock(LockType.READ)
public class LightSessionBean implements LightSessionBeanLocal {
    public static final int TETA_SPLIT = 500;
    
    @EJB
    private LookupSessionBeanLocal lookupSessionBean;

    /**
     * The period to update the tree height in the master node
     */
    private static final int PERIOD = 30; //seconds
    
    DataManagerChordSessionBeanLocal dataManagerChordSessionBean = lookupDataManagerChordSessionBeanLocal();
    
    /** We know the key of the MASTER NODE, just for simplicity we are using a Key that is just 1_16 less than
     * MASTER_NODE key
     */
    private static final Key TREE_HEIGHT_KEY = new Key("66c00da7ad20fdea56d2879af8a8ea43890c390c");
    
    /**
     * Initial tree height
     */
    private int treeHeight = 2;
    
    @Resource
    private SessionContext context;
    
    @PostConstruct  
    public void init () {
        setRemoteTreeHeight();
        TimerService timerService = context.getTimerService();
        timerService.getTimers().forEach((Timer t) -> t.cancel());
        timerService.createIntervalTimer(2030, PERIOD * 1000, new TimerConfig("TREEHEIGHT", true));
    }
    
    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().equals("TREEHEIGHT")) {
            setRemoteTreeHeight();
        }
    }

    /**
     * This is called periodically, it sets the treeHeight consistent between the DataManager and the distributed DB
     */
    private void setRemoteTreeHeight() {
        // This is a very bad workaround but it works and to find a good solution is beyond the purposes of this work
        List<GenericValue> heightBucket = dataManagerChordSessionBean.lookup(TREE_HEIGHT_KEY);
        long h = 0;
        if (heightBucket.size() > 0) {
            // The timestamp of this "special" GenericStat stores the height of the tree
            h = ((GenericStat)heightBucket.get(0)).getTimestamp();
        } 
        if (h < treeHeight) {
            dataManagerChordSessionBean.update(TREE_HEIGHT_KEY, new GenericStat(treeHeight, "TREE", TREE_HEIGHT_KEY));
        }
        
        if (h > treeHeight) {
            setTreeHeight((int)h);    
        }
    }
    
    /**
     * Called to check, in run-time, the height of the tree using the length of
     * a Label
     * @param label  |
     */
    @Override
    public void checkTreeHeight(Label label) {
        int currentHeight = getTreeHeight();
        int max = Math.max(label.getLength(), currentHeight);
        if (currentHeight < max)
           setTreeHeight(max);
    }
    
    @Override
    public int getTreeHeight() {
        return treeHeight;
    }
    /**
     * Updates the treeHeight with the given argument
     * @param treeHeight  |
     */
    @Lock(LockType.WRITE)
    @Override
    public void setTreeHeight(int treeHeight) {
        this.treeHeight = treeHeight;
    }


    /**
     * Split the distributed tree and migrates the datas in the new Bucket (s)
     * @param localBucket |
     * @param timestamp |
     * @param elem |
     * @return | 
     */
    @Lock(LockType.WRITE)
    @AccessTimeout(value = 20, unit = TimeUnit.SECONDS)
    @Override
    public Bucket splitAndPut(Bucket localBucket, long timestamp, GenericStat elem) {
        System.err.println("SPLITTING");
        Label localLabel = localBucket.getLeafLabel();
        Range localRange = localBucket.getRange();
        int   currentRecords = localBucket.getRecordsCounter();
        List<GenericValue> currentDatas = lookupSessionBean.lightLookupAndGetDataBucket(localLabel);
        // The tree was already splitted
        if (currentDatas.size() < TETA_SPLIT)
            return localBucket;
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
        //lightSessionBean.checkTreeHeight(leftPointer.getLeafLabel());
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
        // update localBucket
        dataManagerChordSessionBean.update(newLocalBucket.getKey(), newLocalBucket);
        
        // put bucket to remoteBucket
        dataManagerChordSessionBean.write(newRemoteBucket.getKey(), newRemoteBucket);

        // TODO
        // put datas (One of the children should not need to put the entire bucket but we need a way to 
        // delete only one half of the datas associated with a given key derived from the localBucket label
        dataManagerChordSessionBean.update(leftPointer.getLeafLabel().toDataKey(), leftDatas);
        dataManagerChordSessionBean.update(rightPointer.getLeafLabel().toDataKey(), rightDatas);
        
        //return the label where the put has to send the new stat
        if (timestamp > mid) {
            return rightPointer;
        } 
        return leftPointer; 
    }
    
    private DataManagerChordSessionBeanLocal lookupDataManagerChordSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DataManagerChordSessionBeanLocal) c.lookup("java:global/datamanager-ear-1.0-SNAPSHOT/datamanager-ejb-1.0-SNAPSHOT/DataManagerChordSessionBean!org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
