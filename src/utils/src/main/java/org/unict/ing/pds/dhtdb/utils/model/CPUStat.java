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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 * Model for the statistics of CPU
 */
public class CPUStat extends GenericStat {
    @JsonProperty("usage")
    private final float usage;

    @JsonCreator
    public CPUStat(@JsonProperty("usage") float usage,
            @JsonProperty("timestamp")    long timestamp,
            @JsonProperty("scannerId")    String scannerId,
            @JsonProperty("key")          Key key) {
        super(timestamp, scannerId, key);
        this.topic     = "cpustat";
        this.usage = usage;
    }

    public float getUsage() {
        return usage;
    }
}
