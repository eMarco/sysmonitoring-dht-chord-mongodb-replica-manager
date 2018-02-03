/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public abstract class GenericValue implements Serializable {
    
    protected Key key;
    
    public GenericValue(Key key) {
        this.key = key;
    }
    
    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
