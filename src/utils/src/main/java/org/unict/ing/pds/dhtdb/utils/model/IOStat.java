/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;


public class IOStat extends GenericStat {
    private final String disk;
    private final float readKBps, writeKBps;

    public IOStat(String disk, float readKBps, float writeKBps, long timestamp, String scannerId) {
        super(timestamp, scannerId);
        this.disk = disk;
        this.readKBps = readKBps;
        this.writeKBps = writeKBps;
    }

    public String getDisk() {
        return disk;
    }

    public float getReadKBps() {
        return readKBps;
    }

    public float getWriteKBps() {
        return writeKBps;
    }
}
