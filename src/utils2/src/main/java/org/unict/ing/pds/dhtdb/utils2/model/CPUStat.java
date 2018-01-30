/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils2.model;


public class CPUStat extends GenericStat {
    private final float usage;

    public CPUStat(float usage, long timestamp, String scannerId) {
        super(timestamp, scannerId);
        this.usage = usage;
    }

    public float getUsage() {
        return usage;
    }
}
