/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replicamanager.restTest;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import org.unict.ing.pds.dhtdb.replica.p2p.LocalNodeSessionBeanLocal;

/**
 * REST Web Service
 *
 * @author aleskandro
 */
@Path("generic")
@RequestScoped
public class GenericResource {

    LocalNodeSessionBeanLocal localNodeSessionBean = lookupLocalNodeSessionBeanLocal();


    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of org.unict.ing.pds.dhtdb.replicamanager.restTest.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getText() {
        //TODO return proper representation object
        String ret = "ciaoasdddasds\n";
        ret += localNodeSessionBean.myTest();
        return ret;
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
    }

    private LocalNodeSessionBeanLocal lookupLocalNodeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (LocalNodeSessionBeanLocal) c.lookup("java:global/replicamanager-ear-1.0-SNAPSHOT/replicamanager-ejb-1.0-SNAPSHOT/LocalNodeSessionBean!org.unict.ing.pds.dhtdb.replica.p2p.LocalNodeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}