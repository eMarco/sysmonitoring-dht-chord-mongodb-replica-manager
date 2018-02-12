/* 
 * Copyright (C) 2018 aleskandro - eMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.unict.ing.pds.light.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;import java.util.Objects;
;

/**
 * Implementation of a discrete interval with the needed operators
 */
public class Range implements Serializable {
    /**
     * The MAXIMUM RANGE of data-keys for the indexing
     */
    public static Range REPRESENTABLE_RANGE = new Range(1517888888L, true, 1540000000L, true);
    public static Range EMPTY_RANGE = new Range(-2, false, -1, false);
    private final Boolean lowerIncluded, upperIncluded;
    private final long lower, upper;
    
    @Override
    public String toString() {
        return "Range{" + "lowerIncluded=" + lowerIncluded + ", upperIncluded=" + upperIncluded + ", lower=" + lower + ", upper=" + upper + '}';
    }
         
    @JsonCreator
    public Range(@JsonProperty("lower")long lower, 
            @JsonProperty("lowerIncluded")Boolean lowerIncluded, 
            @JsonProperty("upper")long upper, 
            @JsonProperty("upperIncluded")Boolean upperIncluded) {
        //if (lower > upper) throw new IllegalArgumentException("Wrong range interval");

        this.lowerIncluded = lowerIncluded;
        this.upperIncluded = upperIncluded;
        this.lower = lower;
        this.upper = upper;
    }

    public Boolean contains(Range range) {
        return (this.intersect(range).equals(range));
    }

    private Boolean isLowerBound(long v) {
        return this.lowerIncluded && v == lower;
    }

    private Boolean isUpperBound(long v) {
        return this.upperIncluded && v == upper;
    }
    
    public Boolean contains(long v) {
        return ((lower < v && v < upper) || isLowerBound(v) || isUpperBound(v));
    }

    public Boolean isContainedIn(Range range) {
        return range.contains(this);
    }

    public Range intersect(Range range) {
        long lowerBound = Math.max(this.lower, range.getLower());
        long upperBound = Math.min(this.upper, range.getUpper());
        if (upperBound < lowerBound) {
            return EMPTY_RANGE;
        }
        return new Range(
                lowerBound,
                this.contains(lowerBound) && range.contains(lowerBound),
                upperBound,
                this.contains(upperBound) && range.contains(upperBound));
    }

    public long getLower() {
        return lower;
    }

    public long getUpper() {
        return upper;
    }
    
    @JsonIgnore 
    public boolean isEmpty() {
        return this.equals(EMPTY_RANGE);   
    }

    public Boolean getLowerIncluded() {
        return this.lowerIncluded;
    }

    public Boolean getUpperIncluded() {
        return this.upperIncluded;
    }

    /**
     * Creates the Range for the left or right child of the Bucket associated with this range
     * @param second trigger the creation of the range for the right child (if true) |
     * @return | a new Range in the interval [lower, lower + (upper - lower) / 2)
     */
    public Range createSplit(Boolean second) {
        long mid = (lower + upper) / 2;
        long lowerBound = (second)?mid:lower;
        long upperBound = (second)?upper:mid;
        return new Range(lowerBound, true, upperBound, false);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.lowerIncluded);
        hash = 67 * hash + Objects.hashCode(this.upperIncluded);
        hash = 67 * hash + (int) (this.lower ^ (this.lower >>> 32));
        hash = 67 * hash + (int) (this.upper ^ (this.upper >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Range other = (Range) obj;
        if (this.lower != other.lower) {
            return false;
        }
        if (this.upper != other.upper) {
            return false;
        }
        if (!Objects.equals(this.lowerIncluded, other.lowerIncluded)) {
            return false;
        }
        if (!Objects.equals(this.upperIncluded, other.upperIncluded)) {
            return false;
        }
        return true;
    }
    
}
