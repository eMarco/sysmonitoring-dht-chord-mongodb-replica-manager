/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 * Models a statistic of the Uptime
 */
public class UptimeStat extends GenericStat {
    private final long seconds;
    private final long minutes;
    private final long hours;
    private final long days;

    public UptimeStat(@JsonProperty("seconds") long seconds,
            @JsonProperty("minutes") long minutes,
            @JsonProperty("hours") long hours,
            @JsonProperty("days") long days,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("scannerId") String scannerId,
            @JsonProperty("key") Key key) {
        super(timestamp, scannerId, key);
        this.topic   = "uptimestat";
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours   = hours;
        this.days    = days;
    }

    public long getSeconds() {
        return seconds;
    }
    
    
}
