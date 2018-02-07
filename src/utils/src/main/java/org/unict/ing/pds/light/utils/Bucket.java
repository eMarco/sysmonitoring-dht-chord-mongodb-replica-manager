/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.unict.ing.pds.dhtdb.utils.dht.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 *
 * @author aleskandro
 */
public class Bucket extends GenericValue {
    private int recordsCounter;
    private Range range;
    private Label leafLabel;
    
    public Bucket(Key key) {
        super(key);
    }
    
    @JsonCreator
    public Bucket(Key key, Range range, Label leafLabel, int recordsCounter) {
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
    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Label getLeafLabel() {
        return leafLabel;
    }

    public void setLeafLabel(Label leafLabel) {
        this.leafLabel = leafLabel;
    }

}
