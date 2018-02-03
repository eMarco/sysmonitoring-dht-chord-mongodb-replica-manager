/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public abstract class GenericValue implements Serializable {
    
    protected String type;
    protected String key;
    
    public GenericValue() {
        this.type = this.getClass().getSimpleName();
        System.out.println("TYPE " + this.type);
    }

    public GenericValue(String key) {
        this();
        this.key = key;
    }
    
    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GenericValue(String key, String type) {
        this.type = type;
        this.key = key;
    }
    
}
