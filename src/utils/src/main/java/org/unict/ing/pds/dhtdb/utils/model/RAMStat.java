/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;


public class RAMStat extends GenericStat {
    private final int free, total, available;

    public RAMStat(int free, int total, int available, long timestamp, String scannerId) {
        super(timestamp, scannerId);
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
