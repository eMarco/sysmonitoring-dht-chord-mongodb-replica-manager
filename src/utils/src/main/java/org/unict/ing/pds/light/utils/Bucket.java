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
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 * A bucket stores some infos for the distributed tree: 
 * - the Label to get the hashed key
 * - the Range to get the interval covered by the leaf
 * - the records counter to deal with splitAndPut
 */
public class Bucket extends GenericValue {
    private int recordsCounter;
    private Range range;
    private Label leafLabel;
    
    public Bucket(Key key) {
        super(key);
    }

    @Override
    public String toString() {
        return "Bucket{" + "recordsCounter=" + recordsCounter + ", range=" + range + ", leafLabel=" + leafLabel + '}';
    }
    
    public Bucket(Key key, Range range, 
            Label leafLabel, 
                    int recordsCounter) {
        super(key);
        this.range = range;
        this.leafLabel = leafLabel;
        this.recordsCounter = recordsCounter;
        
    }



    public Bucket(Range range, Label leafLabel, int recordsCounter) {
        super(leafLabel.toKey());
        this.leafLabel = leafLabel;
        this.recordsCounter = recordsCounter;
        this.range = range;
    }

    public Bucket(Range range, Label leafLabel) {
        super(leafLabel.toKey());
        this.range = range;
        this.recordsCounter = 0;        
    }

    public Bucket() {
        super(new Key(""));
    }
    
    public int getRecordsCounter() {
        return recordsCounter;
    }

    public void setRecordsCounter(int recordsCounter) {
        this.recordsCounter = recordsCounter;
    }

    public void incrementRecordsCounter(int i) {
        this.recordsCounter += i;
    }
    
    public void incrementRecordsCounter() {
        this.recordsCounter++;
    }
    
    @JsonIgnore
    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @JsonIgnore
    public Label getLeafLabel() {
        return leafLabel;
    }

    public void setLeafLabel(Label leafLabel) {
        this.leafLabel = leafLabel;
    }
    
    // Methods for JSON to Jongo to Mongo
    @JsonCreator 
    public Bucket(@JsonProperty("key")Key key,
            @JsonProperty("lower")long lower, 
            @JsonProperty("upper")long upper, 
            @JsonProperty("label")String label, 
            @JsonProperty("recordsCounter") int recordsCounter) {
        super(key);
        this.range = new Range(lower, true, upper, false);
        this.leafLabel = new Label(label);
        this.recordsCounter = recordsCounter;
    }
    
    @JsonProperty("upper")
    public long getUpper() {
        if (this.range != null)
            return this.range.getUpper();
        return 0;
    }
    
    @JsonProperty("lower")
    public long getLower() {
        if (this.range != null)
            return this.range.getLower();
        return 0;
    }
    
    @JsonProperty("label")
    public String getLabel() {
        if (this.leafLabel != null)
            return this.leafLabel.getLabel();
        return null;
    }
    
}
