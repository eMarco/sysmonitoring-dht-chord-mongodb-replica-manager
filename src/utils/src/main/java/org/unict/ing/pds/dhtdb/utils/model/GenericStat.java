package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/***
 * 
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@JsonSubTypes({
    @JsonSubTypes.Type(value = CPUStat.class),
    @JsonSubTypes.Type(value = IOStat.class),
    @JsonSubTypes.Type(value = NetworkStat.class),
    @JsonSubTypes.Type(value = RAMStat.class),
    @JsonSubTypes.Type(value = UptimeStat.class)
})
public abstract class GenericStat extends GenericValue {
    private final long timestamp;
    private final String scannerId;

    public GenericStat(long timestamp, String scannerId, Key key) {
        super(key);
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
