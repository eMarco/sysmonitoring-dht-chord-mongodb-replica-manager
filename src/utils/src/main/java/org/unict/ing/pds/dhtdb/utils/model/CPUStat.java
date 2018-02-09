/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 * Model for the statistics of CPU
 */
public class CPUStat extends GenericStat {
    @JsonProperty("usage")
    private final float usage;

    @JsonCreator
    public CPUStat(@JsonProperty("usage") float usage,
            @JsonProperty("timestamp")    long timestamp,
            @JsonProperty("scannerId")    String scannerId,
            @JsonProperty("key")          Key key) {
        super(timestamp, scannerId, key);
        this.topic     = "cpustat";
        this.usage = usage;
    }

    public float getUsage() {
        return usage;
    }
}
