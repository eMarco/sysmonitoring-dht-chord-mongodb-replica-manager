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
        if (lower > upper) throw new IllegalArgumentException("Wrong range interval");

        this.lowerIncluded = lowerIncluded;
        this.upperIncluded = upperIncluded;
        this.lower = lower;
        this.upper = upper;
    }

    public Boolean contains(Range range) {
        Boolean lower, upper;

        lower = ((this.lower == range.lower && ((this.lowerIncluded & range.lowerIncluded) || !range.lowerIncluded))
                || this.lower < range.lower);

        upper = ((this.upper == range.upper && ((this.upperIncluded & range.upperIncluded) || !range.upperIncluded))
                || this.upper > range.upper);

        return lower && upper;
    }

    public Boolean contains(long timestamp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Boolean isContainedIn(Range range) {
        return range.contains(this);
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

    public Boolean getLowerIncluded() {
        return this.lowerIncluded;
    }

    public Boolean getUpperIncluded() {
        return this.upperIncluded;
    }
}
