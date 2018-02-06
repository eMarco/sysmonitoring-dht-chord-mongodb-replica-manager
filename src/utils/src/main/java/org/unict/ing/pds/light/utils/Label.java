/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.light.utils;

import java.util.Collection;
import org.unict.ing.pds.dhtdb.utils.dht.Key;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
public class Label {
    private String label;

    public Label(String label) {
        this.label = label;
    }

    public String getLabel() {
        return "#" + this.label;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.getLabel();
    }

    public Label toDHTKey() {
        return Label.namingFunction(this);
    }

    public Key toKey() {
        return new Key(toDHTKey().getLabel(), true);
    }

    public static Label namingFunction(Label label) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Label nextNamingFunction(Label label) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Label lowestCommonAncestor(Collection<Label> labels) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Range interval() {
        return interval(Range.REPRESENTABLE_RANGE);
    }

    // TODO : rename maximum_range!!
    public static Range interval(Range maximum_range) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
