/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

import javax.ejb.Local;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Local
public interface DataManagerSessionBeanLocal {

    void put(String scanner, String topic, String content);

    String get(String scanner, String topic, String tsStart, String tsEnd);
}
