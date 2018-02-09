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
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/***
 * Class for a GenericStat (extended by the specific stats)
 */
@JsonSubTypes({
    @JsonSubTypes.Type(value = CPUStat.class),
    @JsonSubTypes.Type(value = IOStat.class),
    @JsonSubTypes.Type(value = RAMStat.class),
    @JsonSubTypes.Type(value = UptimeStat.class),
})
public class GenericStat extends GenericValue {
    private long timestamp;

    private String scannerId;
    
    @JsonIgnore
    protected String topic;
    
    @JsonIgnore
    public String getTopic() {
        return topic;
    }
    
    public GenericStat(long timestamp, String scannerId, Key key) {
        super(key);
        this.topic     = "generic";
        this.timestamp = timestamp;
        this.scannerId = scannerId;
    }

    public GenericStat(long timestamp) {
        super(new Key(""));
        this.timestamp = timestamp;
        
    }
    public GenericStat(){
        super(new Key(""));
    }
    
    public String getScannerId() {
        return scannerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setScannerId(String scannerId) {
        this.scannerId = scannerId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final GenericStat other = (GenericStat) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        return true;
    }
    
    
}
