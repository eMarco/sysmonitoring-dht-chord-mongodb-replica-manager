/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;


public class NetworkStat extends GenericStat {
    private final String interf;
    
    // TODO : Float?
    private final String sent, received;

    public NetworkStat(
            @JsonProperty("interf")    String interf, 
            @JsonProperty("sent")      String sent, 
            @JsonProperty("received")  String received, 
            @JsonProperty("timestamp") long timestamp, 
            @JsonProperty("scannerId") String scannerId, 
            @JsonProperty("key")       Key key) {
        super(timestamp, scannerId, key);
        this.interf = interf;
        this.sent = sent;
        this.received = received;
    }

    public String getInterf() {
        return interf;
    }

    public String getSent() {
        return sent;
    }

    public String getReceived() {
        return received;
    }    
}
