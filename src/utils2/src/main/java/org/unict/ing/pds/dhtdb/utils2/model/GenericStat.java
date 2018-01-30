package org.unict.ing.pds.dhtdb.utils2.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/***
 * 
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public abstract class GenericStat {
    private final long timestamp;
    private String scannerId;

    public GenericStat(long timestamp, String scannerId) {
        this.timestamp = timestamp;
        this.scannerId = scannerId;
    }

    public String getScannerId() {
        return scannerId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }    
}
