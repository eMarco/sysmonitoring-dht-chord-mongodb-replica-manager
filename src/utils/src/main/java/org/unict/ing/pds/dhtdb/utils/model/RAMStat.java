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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 * Models a statistic of memory usage
 */
public class RAMStat extends GenericStat {
    private final int memFree, memTotal, memAvailable;

    public RAMStat(
            @JsonProperty("MemFree")      int free,
            @JsonProperty("MemTotal")     int total,
            @JsonProperty("MemAvailable") int available,
            @JsonProperty("timestamp")    long timestamp,
            @JsonProperty("scannerId")    String scannerId,
            @JsonProperty("key")          Key key) {
        super(timestamp, scannerId, key);
        this.topic        = "ramstat";
        this.memFree      = free;
        this.memTotal     = total;
        this.memAvailable = available;
    }

    public int getMemFree() {
        return memFree;
    }

    public int getMemTotal() {
        return memTotal;
    }

    public int getMemAvailable() {
        return memAvailable;
    }
}
