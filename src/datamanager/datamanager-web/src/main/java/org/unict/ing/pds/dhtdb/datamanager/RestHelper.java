/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

/**
 *
 * @author aleskandro
 */
public final class RestHelper {
    public final static String ts(String ts) {
        return ts.length()>1?ts.substring(1):null;
    }
}
