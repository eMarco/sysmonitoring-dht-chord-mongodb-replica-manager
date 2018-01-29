/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.unict.ing.pds.dhtdb.replica.p2p.BaseNode;

/**
 *
 * @author Marco Grassia <marco.grassia@studium.unict.it>
 */
@Path("/RestAPI")
public class RestAPI {
    BaseNode nodeRemote = lookupNodeRemote();

    /**
     *
     * @return
     */
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String showAll() {
        
        return "{}";
    }
    
    /**
     *
     * @return
     */
    @POST
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String postAll() {
        
        return "{}";
    }
    
    /**
     *
     * @param node
     * @return
     */
    @POST
    @Path("/bootstrap")
    @Produces(MediaType.TEXT_PLAIN)
    public String bootStrap(String node) {
        
        
//        nodeRemote.bootstrap(node);
        
        return "{}";
    }
    
    
    private BaseNode lookupNodeRemote() {
        try {
            Context c = new InitialContext();
            return (BaseNode) c.lookup("java:global/replicamanager/replicamanager-ejb/Node!org.unict.ing.pds.dhtdb.replica.p2p.NodeRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
