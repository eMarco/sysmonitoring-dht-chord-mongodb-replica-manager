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
 * Models a statistic of the Uptime
 */
public class UptimeStat extends GenericStat {
    private final long seconds;
    private final long minutes;
    private final long hours;
    private final long days;

    public UptimeStat(@JsonProperty("seconds") long seconds,
            @JsonProperty("minutes") long minutes,
            @JsonProperty("hours") long hours,
            @JsonProperty("days") long days,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("scannerId") String scannerId,
            @JsonProperty("key") Key key) {
        super(timestamp, scannerId, key);
        this.topic   = "uptimestat";
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours   = hours;
        this.days    = days;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getHours() {
        return hours;
    }

    public long getDays() {
        return days;
    }
    
    
}
