/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.pds.dhtdb.replicamanager.rest;

import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.unict.ing.pds.dhtdb.utils.replicamanager.Key;
import org.unict.ing.pds.dhtdb.utils.model.GenericValue;
import org.unict.ing.pds.dhtdb.replica.p2p.NodeSessionBeanLocal;
import org.unict.ing.pds.dhtdb.utils.replicamanager.NodeReference;
import org.unict.ing.pds.dhtdb.utils.replicamanager.RemoteNodeProxy;

/**
 * REST Web Service
 *
 * @author aleskandro
 */
@Path(RemoteNodeProxy.PATH)
@RequestScoped
public class RestAPI {

    NodeSessionBeanLocal nodeSessionBean = lookupNodeSessionBeanLocal();


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
    @POST
    @Path(value="{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String put(@PathParam(value="key") String k, String u) {

        try {
            GenericValue genericValue = new Gson().fromJson(u, GenericValue.class);

            Class<? extends GenericValue> t = Class.forName("org.unict.ing.pds.dhtdb.utils.model." + genericValue.getType()).asSubclass(GenericValue.class);
            GenericValue value = new Gson().fromJson(u, t);

            Key key = new Key(k, false);

            nodeSessionBean.put(key, value);
            return key + " " + value;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RestAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "ERROR";
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
        Key key = new Key(k, false);

        List<String> ret = new LinkedList<>();
        for (GenericValue v : nodeSessionBean.get(key) ) {
            ret.add(new Gson().toJson(v));
        }

        return new Gson().toJson(ret);
    }

    /**
     * Retrieves successor of given Key
     * @param k
     * @return an instance of java.lang.String
     */
    @GET
    @Path(value="/successor/{key : ([A-Za-z0-9]+)}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String findSuccessor(@PathParam(value="key") String k) {
        
        Key key = new Key(k, false);

        return new Gson().toJson(nodeSessionBean.findSuccessor(key));
    }

    /**
     * 
     * @param u
     * @return an instance of java.lang.String
     */
    @POST
    @Path(value="/notify")
    @Consumes(MediaType.TEXT_PLAIN)
    public String notify(String u) {
        NodeReference nodeRef = new Gson().fromJson(u, NodeReference.class);

        return new Gson().toJson(nodeSessionBean.notify(nodeRef));
    }


    private NodeSessionBeanLocal lookupNodeSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (NodeSessionBeanLocal) c.lookup("java:global/replicamanager-ear-1.0-SNAPSHOT/replicamanager-ejb-1.0-SNAPSHOT/NodeSessionBean!org.unict.ing.pds.dhtdb.replica.p2p.NodeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    @GET
    @Path(value="/ping")
    @Consumes(MediaType.TEXT_PLAIN) 
    public String ping() {
        return new Gson().toJson(nodeSessionBean.getNodeReference());
    }
}
