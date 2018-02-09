/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 * GenericValue has only a Key, extending this class you can creates your model for several kind of Datas
 * Created to make Chord specific-data-structure agnostic 
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
