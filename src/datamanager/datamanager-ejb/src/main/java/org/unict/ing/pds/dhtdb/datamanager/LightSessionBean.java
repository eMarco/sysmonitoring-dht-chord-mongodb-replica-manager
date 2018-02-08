/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author aleskandro
 */
@Singleton
@Lock(LockType.READ)
public class LightSessionBean implements LightSessionBeanLocal {

    /**
     * CONFIG VARS
     */
    private static final int PERIOD = 30; //seconds
    DataManagerChordSessionBeanLocal dataManagerChordSessionBean = lookupDataManagerChordSessionBeanLocal();
    private static final Key TREE_HEIGHT_KEY = new Key("66c00da7ad20fdea56d2879af8a8ea43890c390c");
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
        //System.err.println("TIMEOUT: " + timer.getInfo());
        if (timer.getInfo().equals("TREEHEIGHT")) {
            setRemoteTreeHeight();
        }
    }

    private void setRemoteTreeHeight() {
        // This is a very bad workaround but it works and to find a good solution is beyond the purposes of this work
        List<GenericValue> heightBucket = dataManagerChordSessionBean.lookup(TREE_HEIGHT_KEY);
        long h = 0;
        if (heightBucket.size() > 0) {
            // The timestamp of this "special" GenericStat stores the height of the tree
            h = ((GenericStat)heightBucket.get(0)).getTimestamp();
            
        } 
        if (h > treeHeight || h == 0) {
            treeHeight = h>treeHeight?(int)h:treeHeight;
            dataManagerChordSessionBean.update(TREE_HEIGHT_KEY, new GenericStat(treeHeight, "TREE", TREE_HEIGHT_KEY));
        }
        
    }
    
    @Override
    public int getTreeHeight() {
        return treeHeight;
    }

    @Lock(LockType.WRITE)
    @Override
    public void setTreeHeight(int treeHeight) {
        this.treeHeight = treeHeight;
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
