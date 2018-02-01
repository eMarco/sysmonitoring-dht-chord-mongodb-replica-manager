/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.utils.model;

import java.io.Serializable;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class GenericValue implements Serializable {
    
    protected String type;
    
    public GenericValue() {
        this.type = this.getClass().getSimpleName();
        System.out.println("TYPE " + this.type);
    }
    
    public String getType() {
        return type;
    }
    
}
