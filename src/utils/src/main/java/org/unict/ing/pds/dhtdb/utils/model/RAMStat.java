/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.dht.Key;


public class RAMStat extends GenericStat {
    private final int memFree, memTotal, memAvailable;

    public RAMStat(
            @JsonProperty("MemFree")      int free,
            @JsonProperty("MemTotal")     int total,
            @JsonProperty("MemAvailable") int available,
            @JsonProperty("timestamp")    long timestamp,
            @JsonProperty("scannerId")    String scannerId,
            @JsonProperty("key")          Key key) {
        super(timestamp, scannerId, key);
        this.memFree = free;
        this.memTotal = total;
        this.memAvailable = available;
    }

    public int getMemFree() {
        return memFree;
    }

    public int getMemTotal() {
        return memTotal;
    }

    public int getMemAvailable() {
        return memAvailable;
    }
}
