/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class Range {
    public static Range REPRESENTABLE_RANGE = new Range(0, Boolean.TRUE, ((2^32)-1), Boolean.TRUE);
    
    private final Boolean lowerIncluded, upperIncluded;
    private final long lower, upper;

    public Range(long lower, Boolean lowerIncluded, long upper, Boolean upperIncluded) {
        this.lowerIncluded = lowerIncluded;
        this.upperIncluded = upperIncluded;
        this.lower = lower;
        this.upper = upper;
    }

    public Boolean contains(Range range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean contains(long timestamp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean isContainedIn(Range range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Range intersect(Range range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public long getLower() {
        return lower;
    }

    public long getUpper() {
        return upper;
    }
    
    public boolean isEmpty() {
        return false;   
    }
}
