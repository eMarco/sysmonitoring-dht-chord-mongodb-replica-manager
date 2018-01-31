/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public final class Key implements Comparable<Key> {
    private final String key;

    public Key(String key) {
        this(key, true);
    }
    
    public Key(String key, Boolean toHash) {
        if (toHash)
            this.key = DigestUtils.sha512Hex(key);
        else
            this.key = key;
    }

    public String getId() {
        return key;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        // TODO : Add modulo?
        return this.key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return key;
    }

}
