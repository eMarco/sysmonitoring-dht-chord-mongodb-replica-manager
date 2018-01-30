/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;


public class UptimeStat extends GenericStat {
    private final long seconds;

    public UptimeStat(long seconds, long timestamp, String scannerId) {
        super(timestamp, scannerId);
        this.seconds = seconds;
    }

    public long getSeconds() {
        return seconds;
    }
}
