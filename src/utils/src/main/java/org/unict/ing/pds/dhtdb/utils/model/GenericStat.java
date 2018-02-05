package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

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
    @JsonSubTypes.Type(value = RAMStat.class),
    @JsonSubTypes.Type(value = UptimeStat.class),
})
public abstract class GenericStat extends GenericValue {
    private long timestamp;

    private String scannerId;

    public GenericStat(long timestamp, String scannerId, Key key) {
        super(key);
        this.timestamp = timestamp;
        this.scannerId = scannerId;
    }
    public GenericStat(){
        super(new Key(""));
    }
    
    public String getScannerId() {
        return scannerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setScannerId(String scannerId) {
        this.scannerId = scannerId;
    }
    
}
