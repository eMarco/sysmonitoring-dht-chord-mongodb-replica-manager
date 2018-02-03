/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;


public class CPUStat extends GenericStat {
    private final float usage;
    
    @JsonCreator
    public CPUStat(@JsonProperty("usage")float usage, 
            @JsonProperty("timestamp")long timestamp, 
            @JsonProperty("scannerId")String scannerId, 
            @JsonProperty("key") String key, 
            @JsonProperty("type") String type) {
        super(timestamp, scannerId, key, "CPUStat");
        this.usage = usage;
    }

    public float getUsage() {
        return usage;
    }
}
