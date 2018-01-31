/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import javax.ejb.Local;

/**
 *
 * @author aleskandro
 */
@Local
public interface LocalNodeSessionBeanLocal {

    public String myTest();

    public NodeReference findSuccessor(NodeReference nodeRef);
    
}
