/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.datamanager;

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

/**
 * REST Web Service
 *
 * @author aleskandro
 */
@Path("generic")
@RequestScoped
public class GenericResource {

//    DataManagerChordSessionBeanLocal chordSessionBean = lookupChordBeanLocal();
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of org.unict.ing.pds.dhtdb.datamanager.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getXml() {
        
        return "Ciao";
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

/*    private DataManagerChordSessionBeanLocal lookupChordBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (DataManagerChordSessionBean) c.lookup("java:global/datamanager-ear-1.0-SNAPSHOT/datamanager-ejb-1.0-SNAPSHOT/DataManagerChordSessionBean!org.unict.ing.pds.dhtdb.datamanager.DataManagerChordSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
*/
}
