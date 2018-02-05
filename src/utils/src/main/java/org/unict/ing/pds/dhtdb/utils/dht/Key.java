/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public final class Key implements Comparable<Key>, Serializable {
    public static final int LENGHT = 160;

    private final String key;

    /**
     * Overloaded constructor for Key (toHash default to True)
     * the key will be hashed
     * @param key
     */
    @JsonCreator
    public Key(@JsonProperty("key") String key) {
        this(key, false);
    }

    /**
     * Constructor for Key
     * @param key
     * @param toHash if true the key argument will be passed to the hash function
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
     * @param pow
     * @return
     */
    public Key sumPow(int pow) {
        return this.sumPowDivided(pow, 1);
    }

    /**
     * Returns this.key + 2^pow/divisor
     * @param pow
     * @param divisor
     * @return
     */
    public Key sumPowDivided(int pow, int divisor) {
        return this.sum(new BigInteger("2", DEC).pow(pow).divide(new BigInteger(String.valueOf(divisor), DEC)));
    }

    /**
     * Returns this.key + integer
     * @param integer
     * @return
     */
    public Key sum(BigInteger integer) {
        return sum(this, integer);
    }

    /**
     * Returns this.key + (Key) b
     * @param b
     * @return
     */
    public Key sum(Key b) {
        return sum(this, b);
    }

    /**
     * Returns (Key) a + (Key) b
     * @param a
     * @param b
     * @return
     */
    public static Key sum(Key a, Key b) {
        return Key.sum(a, new BigInteger(b.key, HEX));
    }

    /**
     * Returns (Key) a + integer
     * @param a
     * @param integer
     * @return
     */
    public static Key sum(Key a, BigInteger integer) {
        return new Key(
                new BigInteger(a.key, HEX)  .add(integer)
                                            .mod(new BigInteger("2", DEC).pow(LENGHT))
                                            .toString(HEX)
                );
    }

}
