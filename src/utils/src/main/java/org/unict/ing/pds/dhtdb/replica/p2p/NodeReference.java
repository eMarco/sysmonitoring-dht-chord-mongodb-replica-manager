/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replica.p2p;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class NodeReference extends BaseNode {
    private String serviceURL;

    @Override
    public void put() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void get() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bootstrap(NodeReference nodeRef) {
        BaseNode nodeRemote = lookupNodeRemote();
        
        nodeRemote.bootstrap(nodeRef);
    }

    @Override
    public NodeReference findSuccessor(NodeReference nodeRef) {
        BaseNode nodeRemote = lookupNodeRemote();
        
        return nodeRemote.findSuccessor(nodeRef);
    }
    
    // TODO: CACHE ME?
    /***
     * Retrieve remote node
     * @return BaseNode
     */
    private BaseNode lookupNodeRemote() {
        Properties props = new Properties();
//        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
//        props.setProperty("org.omg.CORBA.ORBInitialHost", "*" + hostname + "*");
//        props.setProperty("org.omg.CORBA.ORBInitialPort", "* " + port + "*");
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        props.put(Context.PROVIDER_URL, serviceURL); //"remote://localhost:4447");
//        // username
//        props.put(Context.SECURITY_PRINCIPAL, "peter");
//        // password
//        props.put(Context.SECURITY_CREDENTIALS, "lois");
        
        try {
            Context c = new InitialContext(props);
             return (BaseNode) c.lookup("java:global/replicamanager/replicamanager-ejb/Node!org.unict.ing.pds.dhtdb.replica.p2p.NodeRemote");
            
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
