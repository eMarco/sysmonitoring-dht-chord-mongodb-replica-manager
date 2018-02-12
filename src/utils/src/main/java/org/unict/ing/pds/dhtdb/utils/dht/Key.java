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
package org.unict.ing.pds.dhtdb.utils.dht;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 */
public final class Key implements Comparable<Key>, Serializable {
    public static final int LENGTH = 160;

    private final String key;

    /**
     * Overloaded constructor for Key (toHash default to True)
     * the key will be hashed
     * @param key |
     */
    @JsonCreator
    public Key(@JsonProperty("key") String key) {
        this(key, false);
    }

    /**
     * Constructor for Key
     * @param key |
     * @param toHash if true the key argument will be passed to the hash function |
     */
    public Key(String key, Boolean toHash) {
        if (toHash)
            this.key = DigestUtils.shaHex(key);
        else
            this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.key);
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
        final Key other = (Key) obj;
        return Objects.equals(this.key, other.key);
    }

    @Override
    public int compareTo(Key o) {
        return this.key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return key;
    }

    private static final int HEX = 16;
    private static final int DEC = 10;
    private static final int BIN = 2;

    /**
     * Returns this.key + 2^pow
     * @param pow |
     * @return |
     */
    public Key sumPow(int pow) {
        return this.sumPowDivided(pow, 1);
    }

    /**
     * Returns this.key + 2^pow/divisor
     * @param pow |
     * @param divisor |
     * @return |
     */
    public Key sumPowDivided(int pow, int divisor) {
        return this.sum(new BigInteger("2", DEC).pow(pow).divide(new BigInteger(String.valueOf(divisor), DEC)));
    }

    /**
     * Returns this.key + integer
     * @param integer |
     * @return |
     */
    public Key sum(BigInteger integer) {
        return sum(this, integer);
    }

    /**
     * Returns this.key + (Key) b
     * @param b |
     * @return |
     */
    public Key sum(Key b) {
        return sum(this, b);
    }

    /**
     * Returns (Key) a + (Key) b
     * @param a |
     * @param b |
     * @return |
     */
    public static Key sum(Key a, Key b) {
        return Key.sum(a, new BigInteger(b.key, HEX));
    }

    /**
     * Returns (Key) a + integer
     * @param a |
     * @param integer |
     * @return |
     */
    public static Key sum(Key a, BigInteger integer) {
        return new Key(
                new BigInteger(a.key, HEX)  .add(integer)
                                            .mod(new BigInteger("2", DEC).pow(LENGTH))
                                            .toString(HEX)
                );
    }

}
