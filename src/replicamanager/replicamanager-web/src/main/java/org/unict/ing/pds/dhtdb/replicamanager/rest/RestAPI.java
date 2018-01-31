/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replicamanager.rest;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.unict.ing.pds.dhtdb.replica.p2p.Key;
import org.unict.ing.pds.dhtdb.replica.p2p.LocalNodeSessionBeanLocal;
import org.unict.ing.pds.dhtdb.replica.p2p.NodeReference;
import org.unict.ing.pds.dhtdb.replica.p2p.NodeSessionBeanRemote;
import org.unict.ing.pds.dhtdb.utils.model.GenericStat;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;

/**
 * REST Web Service
 *
 * @author aleskandro
 */
@Path("/replicamanager")
@RequestScoped
public class RestAPI {

    NodeSessionBeanRemote nodeSessionBean = lookupNodeSessionBeanRemote();

    LocalNodeSessionBeanLocal localNodeSessionBean = lookupLocalNodeSessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RestAPI
     */
    public RestAPI() {
    }

    /**
     * Put data
     * @param k
     * @param u
     * @return an instance of java.lang.String
     */
    @PUT
    @Path(value="{key : /([A-Za-z0-9]+)}")   
    @Consumes(MediaType.TEXT_PLAIN)
    public String put(@PathParam(value="key") String k, String u) {
        GenericStat value = new Gson().fromJson(u, GenericStat.class);
        Key key = new Key(k);
        
        nodeSessionBean.put(key, value);
        return key + " " + value;
    }
    
    /**
     * Get Data
     * @param k
     * @return an instance of java.lang.String
     */
    @GET
    @Path(value="{key : ([A-Za-z0-9]+)}")        
    @Consumes(MediaType.TEXT_PLAIN)
    public String get(@PathParam(value="key") String k) {
        Key key = new Key(k);
        
        System.out.println("RECEIVED REQUEST FOR KEY " + key.toString());
        System.out.println(nodeSessionBean.get(key));
        
        return new Gson().toJson(nodeSessionBean.get(key));
    }

    /**
     * Retrieves representation of an instance of org.unict.ing.pds.dhtdb.replicamanager.rest.RestAPI
     * @param u
     * @return an instance of java.lang.String
     */
    @POST
    @Path("/successor")
    @Consumes(MediaType.TEXT_PLAIN)
    public String findSuccessor(String u) {
        NodeReference nodeRef = new Gson().fromJson(u, NodeReference.class);
        
        return new Gson().toJson(localNodeSessionBean.findSuccessor(nodeRef));
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

    private NodeSessionBeanRemote lookupNodeSessionBeanRemote() {
        try {
            javax.naming.Context c = new InitialContext();
            return (NodeSessionBeanRemote) c.lookup("java:global/replicamanager-ear-1.0-SNAPSHOT/replicamanager-ejb-1.0-SNAPSHOT/NodeSessionBean!org.unict.ing.pds.dhtdb.replica.p2p.NodeSessionBeanRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
