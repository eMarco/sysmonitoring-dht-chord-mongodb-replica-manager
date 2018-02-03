/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;


public class RAMStat extends GenericStat {
    private final int free, total, available;

    public RAMStat(
            @JsonProperty("free")      int free,
            @JsonProperty("total")     int total,
            @JsonProperty("available") int available,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("scannerId") String scannerId,
            @JsonProperty("key")       Key key) {
        super(timestamp, scannerId, key);
        this.free = free;
        this.total = total;
        this.available = available;
    }

    public int getFree() {
        return free;
    }

    public int getTotal() {
        return total;
    }

    public int getAvailable() {
        return available;
    }
}
